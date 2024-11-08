<template>
  <div style="width: 100%; min-height: 100vh; display: inline-flex">
    <div class="messages">
      <div v-for="(message, index) in messages" :key="index" :title="JSON.stringify(message)">
        <div class="message">
          <div v-if="message.source === 'AIRPORT'">
            <span class="message-header">{{ message.airportName }}</span>
            <span class="message-detail">Airport</span>
          </div>
          <div v-if="message.source === 'OFFICE' && message.messageType === 'ROUTE'">
            <span class="message-header">Route</span>
            <span class="message-detail">&#9758;</span>
          </div>
          <div v-if="message.source === 'OFFICE' && message.messageType === 'STATE'">
            <span class="message-header">Status</span>
            <span class="message-detail">?</span>
          </div>
          <div v-if="message.source === 'PLANE'">
            <span class="message-header">{{ message.planeName }} - &#9992;</span>
            <div v-if="message.route">
              <span>From: {{ message.route.from.name }} to {{ message.route.to.name }}</span>
              <span>Progress: {{ message.route.progress }}%</span>
            </div>
          </div>
          <div class="message-date">{{ message.timestamp }}</div>
        </div>
      </div>
    </div>

    <div class="radar">
      <div style="padding: 10px; text-align: center; color: red; font-size: 200%">
        <span v-for="(route, index) in tempRoute">
          {{ route }} <b v-if="index !== tempRoute.length - 1">&rtri;</b>
        </span>
        <button @click="submitRoute" v-if="tempRoute.length > 1" class="roundButton">&check;</button>
        <button @click="cancelRoute" v-if="tempRoute.length > 0" class="roundButton">&cross;</button>
      </div>
      <Airport v-for="airport in airports" :key="airport.name" :airport="airport" :click-callback="addRoute"/>
      <div class="planeArea">
        <Plane v-for="(row, index) in planes" :key="index" :level="index" :plane="row"></Plane>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Plane from "../components/Plane.vue";
import Airport from "../components/Airport.vue";

export default {
  components: { Plane, Airport },
  data() {
    return {
      socket: null,
      airports: [],
      tempRoute: [],
      planes: [],
      messages: []
    }
  },

  mounted() {
    this.defineSocket();
    setInterval(this.wakeSocketUp, 500);
  },

  methods: {
    defineSocket() {
      this.socket = new WebSocket("ws://localhost:8081/websocket");

      this.socket.onopen = () => {
        console.log("WebSocket connected");

        this.socket.onmessage = (_message) => {
          let messageAsJson = JSON.parse(_message.data);

          // Додати час для кожного повідомлення
          messageAsJson.timestamp = new Date().toLocaleString();

          if (messageAsJson.source === "AIRPORT") {
            this.updateAirportMessage(messageAsJson);
          }

          if (messageAsJson.source === "PLANE" && messageAsJson.messageType === "STATE") {
            this.updatePlaneMessage(messageAsJson);
          }

          this.messages.unshift(messageAsJson);

          if (this.messages.length > 14) {
            this.messages.splice(14);
          }
        };
      };

      this.socket.onclose = () => {
        console.log("WebSocket connection closed, attempting to reconnect...");
        setTimeout(this.defineSocket, 500);
      };

      this.socket.onerror = (error) => {
        console.error("WebSocket error:", error);
        this.socket.close();
      };
    },

    wakeSocketUp() {
      if (this.socket && this.socket.readyState === WebSocket.OPEN) {
        if (this.airports.length === 0) {
          this.socket.send("update");
        }
      } else {
        this.defineSocket();
      }
    },

    updateAirportMessage(messageAsJson) {
      messageAsJson.airportName = messageAsJson.airport.name;

      // Видаляємо логіку з масивами літаків
      // Не використовуємо масиви для відлітаючих, прилітаючих чи тих, що знаходяться в аеропорту

      // Оновлюємо аеропорт в списку
      this.setAirport(messageAsJson.airport);
    },

    updatePlaneMessage(messageAsJson) {
      messageAsJson.planeName = messageAsJson.plane.name;
      messageAsJson.route = messageAsJson.plane.route.directions[0];

      // Оновлюємо інформацію про літак у масиві літаків
      this.setPlane(messageAsJson.plane);
    },

    setAirport(airport) {
      let index = this.airports.findIndex(row => row.name === airport.name);
      if (index >= 0) {
        this.airports.splice(index, 1, airport);
      } else {
        this.airports.push(airport);
      }
    },

    addRoute(airport) {
      // Перевірка на те, що вибраний аеропорт не є таким самим, як останній доданий
      if (this.tempRoute.length > 0 && this.tempRoute[this.tempRoute.length - 1] === airport.name) {
        return;
      }

      // Додаємо аеропорт, якщо їх ще менше ніж два
      if (this.tempRoute.length < 2) {
        this.tempRoute.push(airport.name);
      }
    },

    submitRoute() {
      axios.post("/api/routes/route", this.tempRoute);
      this.tempRoute = [];
    },

    cancelRoute() {
      this.tempRoute = [];
    },

    setPlane(plane) {
      const existsIndex = this.planes.findIndex(row => row.name === plane.name);

      if (existsIndex >= 0) {
        this.planes.splice(existsIndex, 1);
      } else if (plane.flying) {
        this.planes.push(plane);
      }
    }
  }
}
</script>


<style>

body {
  overflow: hidden;
  margin: 0;
}

.message-header {
  font-size: 14px;
  font-weight: bold;
}

.message-detail {
  font-size: 12px;
  color: #777;
}

.message-date {
  font-size: 10px;
  color: gray;
  text-align: center;
}

.messages {
  padding: 10px;
  width: 11%;
  background: burlywood;
  max-height: 95vh;
  overflow-y: auto;
}

.radar {
  width: 100%;
  height: 95vh;
  position: relative;
  background: url("assets/field.png") no-repeat center center;
  background-size: cover;
}

.roundButton {
  padding: 4px 8px;
  border-bottom: wheat 2px solid;
  border-radius: 4px;
  background: wheat;
  color: blue;
  cursor: pointer;
  opacity: .8;
}

.roundButton:hover {
  opacity: 1;
}

.planeArea {
  position: absolute;
  right: 0;
  left: 0;
  top: 0;
  bottom: 0;
  pointer-events: none;
}

.message {
  border-radius: 10px;
  background: white;
  line-height: 20px;
  color: #232323;
  font-size: 12px;
  margin: 10px auto;
  padding: 5px;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

span b {
  font-size: 80%;
  color: brown;
}

</style>










<!--<template>-->
<!--  <div style="width: 100%; min-height: 100vh; display: inline-flex">-->
<!--    <div class="messages">-->
<!--      <div v-for="(message, index) in messages" :key="index" :title="JSON.stringify(message)">-->
<!--        <div class="message">-->
<!--          <div v-if="message.source === 'AIRPORT'">-->
<!--            <span class="message-header">{{ message.airportName }}</span>-->
<!--            <span class="message-detail">Airport</span>-->
<!--            <div v-if="message.departingPlanes.length > 0">-->
<!--              <span><strong>Departing planes:</strong> {{ message.departingPlanes.join(', ') }}</span>-->
<!--            </div>-->
<!--            <div v-if="message.arrivingPlanes.length > 0">-->
<!--              <span><strong>Arriving planes:</strong> {{ message.arrivingPlanes.join(', ') }}</span>-->
<!--            </div>-->
<!--          </div>-->
<!--          <div v-if="message.source === 'OFFICE' && message.messageType === 'ROUTE'">-->
<!--            <span class="message-header">Route</span>-->
<!--            <span class="message-detail">&#9758;</span>-->
<!--          </div>-->
<!--          <div v-if="message.source === 'OFFICE' && message.messageType === 'STATE'">-->
<!--            <span class="message-header">Status</span>-->
<!--            <span class="message-detail">?</span>-->
<!--          </div>-->
<!--          <div v-if="message.source === 'PLANE'">-->
<!--            <span class="message-header">{{ message.planeName }} - &#9992;</span>-->
<!--            <div v-if="message.route">-->
<!--              <span>From: {{ message.route.from.name }} to {{ message.route.to.name }}</span>-->
<!--              <span>Progress: {{ message.route.progress }}%</span>-->
<!--            </div>-->
<!--          </div>-->
<!--          <div class="message-date">{{ message.timestamp }}</div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

<!--    <div class="radar">-->
<!--      <div style="padding: 10px; text-align: center; color: red; font-size: 200%">-->
<!--        <span v-for="(route, index) in tempRoute">-->
<!--          {{ route }} <b v-if="index !== tempRoute.length - 1">&rtri;</b>-->
<!--        </span>-->
<!--        <button @click="submitRoute" v-if="tempRoute.length > 1" class="roundButton">&check;</button>-->
<!--        <button @click="cancelRoute" v-if="tempRoute.length > 0" class="roundButton">&cross;</button>-->
<!--      </div>-->
<!--      <Airport v-for="airport in airports" :key="airport.name" :airport="airport" :click-callback="addRoute"/>-->
<!--      <div class="planeArea">-->
<!--        <Plane v-for="(row, index) in planes" :key="index" :level="index" :plane="row"></Plane>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import axios from 'axios';-->
<!--import Plane from "../components/Plane.vue";-->
<!--import Airport from "../components/Airport.vue";-->

<!--export default {-->
<!--  components: { Plane, Airport },-->
<!--  data() {-->
<!--    return {-->
<!--      socket: null,-->
<!--      airports: [],-->
<!--      tempRoute: [],-->
<!--      planes: [],-->
<!--      messages: []-->
<!--    }-->
<!--  },-->

<!--  mounted() {-->
<!--    this.defineSocket();-->
<!--    setInterval(this.wakeSocketUp, 500);-->
<!--  },-->

<!--  methods: {-->
<!--    defineSocket() {-->
<!--      this.socket = new WebSocket("ws://localhost:8081/websocket");-->

<!--      this.socket.onopen = () => {-->
<!--        console.log("WebSocket connected");-->

<!--        this.socket.onmessage = (_message) => {-->
<!--          let messageAsJson = JSON.parse(_message.data);-->

<!--          // Додати час для кожного повідомлення-->
<!--          messageAsJson.timestamp = new Date().toLocaleString();-->

<!--          if (messageAsJson.source === "AIRPORT") {-->
<!--            this.updateAirportMessage(messageAsJson);-->
<!--          }-->

<!--          if (messageAsJson.source === "PLANE" && messageAsJson.messageType === "STATE") {-->
<!--            this.updatePlaneMessage(messageAsJson);-->
<!--          }-->

<!--          this.messages.unshift(messageAsJson);-->

<!--          if (this.messages.length > 14) {-->
<!--            this.messages.splice(14);-->
<!--          }-->
<!--        };-->
<!--      };-->

<!--      this.socket.onclose = () => {-->
<!--        console.log("WebSocket connection closed, attempting to reconnect...");-->
<!--        setTimeout(this.defineSocket, 500);-->
<!--      };-->

<!--      this.socket.onerror = (error) => {-->
<!--        console.error("WebSocket error:", error);-->
<!--        this.socket.close();-->
<!--      };-->
<!--    },-->

<!--    wakeSocketUp() {-->
<!--      if (this.socket && this.socket.readyState === WebSocket.OPEN) {-->
<!--        if (this.airports.length === 0) {-->
<!--          this.socket.send("update");-->
<!--        }-->
<!--      } else {-->
<!--        this.defineSocket();-->
<!--      }-->
<!--    },-->

<!--    updateAirportMessage(messageAsJson) {-->
<!--      messageAsJson.airportName = messageAsJson.airport.name;-->

<!--      // Створюємо масиви для літаків, що вилітають і прилітають-->
<!--      messageAsJson.departingPlanes = [];-->
<!--      messageAsJson.arrivingPlanes = [];-->

<!--      // Перевіряємо, які літаки вилітають і які прилітають-->
<!--      this.planes.forEach(plane => {-->
<!--        if (plane.route && plane.route.directions.length > 0) {-->
<!--          const direction = plane.route.directions[0];-->
<!--          const fromAirport = direction.from.name;-->
<!--          const toAirport = direction.to.name;-->
<!--          const progress = direction.progress;-->

<!--          if (fromAirport === messageAsJson.airportName && progress < 100) {-->
<!--            messageAsJson.departingPlanes.push(plane.name);-->
<!--          }-->

<!--          if (toAirport === messageAsJson.airportName && progress >= 100) {-->
<!--            messageAsJson.arrivingPlanes.push(plane.name);-->
<!--          }-->
<!--        }-->
<!--      });-->

<!--      this.setAirport(messageAsJson.airport);-->
<!--    },-->

<!--    updatePlaneMessage(messageAsJson) {-->
<!--      messageAsJson.planeName = messageAsJson.plane.name;-->
<!--      messageAsJson.route = messageAsJson.plane.route.directions[0];-->

<!--      // Оновлюємо інформацію про літак у масиві літаків-->
<!--      this.setPlane(messageAsJson.plane);-->
<!--    },-->

<!--    setAirport(airport) {-->
<!--      let index = this.airports.findIndex(row => row.name === airport.name);-->
<!--      if (index >= 0) {-->
<!--        this.airports.splice(index, 1, airport);-->
<!--      } else {-->
<!--        this.airports.push(airport);-->
<!--      }-->
<!--    },-->

<!--    addRoute(airport) {-->
<!--      if (this.tempRoute.length > 0 && this.tempRoute[this.tempRoute.length - 1] === airport.name) {-->
<!--        return;-->
<!--      }-->
<!--      this.tempRoute.push(airport.name);-->
<!--    },-->

<!--    submitRoute() {-->
<!--      axios.post("/api/routes/route", this.tempRoute);-->
<!--      this.tempRoute = [];-->
<!--    },-->

<!--    cancelRoute() {-->
<!--      this.tempRoute = [];-->
<!--    },-->

<!--    setPlane(plane) {-->
<!--      const existsIndex = this.planes.findIndex(row => row.name === plane.name);-->

<!--      if (existsIndex >= 0) {-->
<!--        this.planes.splice(existsIndex, 1, plane);-->

<!--      } else if (plane.flying) {-->
<!--        this.planes.push(plane);-->
<!--      }-->
<!--    }-->


<!--  }-->
<!--}-->
<!--</script>-->

<!--<style>-->

<!--body {-->
<!--  overflow: hidden;-->
<!--  margin: 0;-->
<!--}-->

<!--.message-header {-->
<!--  font-size: 14px;-->
<!--  font-weight: bold;-->
<!--}-->

<!--.message-detail {-->
<!--  font-size: 12px;-->
<!--  color: #777;-->
<!--}-->

<!--.message-date {-->
<!--  font-size: 10px;-->
<!--  color: gray;-->
<!--  text-align: center;-->
<!--}-->

<!--.messages {-->
<!--  padding: 10px;-->
<!--  width: 11%;-->
<!--  background: burlywood;-->
<!--  max-height: 95vh;-->
<!--  overflow-y: auto;-->
<!--}-->

<!--.radar {-->
<!--  width: 100%;-->
<!--  height: 95vh;-->
<!--  position: relative;-->
<!--  background: url("assets/field.png") no-repeat center center;-->
<!--  background-size: cover;-->
<!--}-->

<!--.roundButton {-->
<!--  padding: 4px 8px;-->
<!--  border-bottom: wheat 2px solid;-->
<!--  border-radius: 4px;-->
<!--  background: wheat;-->
<!--  color: blue;-->
<!--  cursor: pointer;-->
<!--  opacity: .8;-->
<!--}-->

<!--.roundButton:hover {-->
<!--  opacity: 1;-->
<!--}-->

<!--.planeArea {-->
<!--  position: absolute;-->
<!--  right: 0;-->
<!--  left: 0;-->
<!--  top: 0;-->
<!--  bottom: 0;-->
<!--  pointer-events: none;-->
<!--}-->

<!--.message {-->
<!--  border-radius: 10px;-->
<!--  background: white;-->
<!--  line-height: 20px;-->
<!--  color: #232323;-->
<!--  font-size: 12px;-->
<!--  margin: 10px auto;-->
<!--  padding: 5px;-->
<!--  text-align: left;-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  justify-content: space-between;-->
<!--}-->

<!--span b {-->
<!--  font-size: 80%;-->
<!--  color: brown;-->
<!--}-->

<!--</style>-->










<!--<template>-->
<!--  <div style="width: 100%; min-height: 100vh; display: inline-flex">-->
<!--    <div class="messages">-->
<!--      <div v-for="(message, index) in messages" :key="index" :title="JSON.stringify(message)">-->
<!--        <div class="message">-->
<!--          <div v-if="message.source === 'AIRPORT'">-->
<!--            <span class="message-header">{{ message.airportName }}</span>-->
<!--            <span class="message-detail">Airport</span>-->
<!--          </div>-->
<!--          <div v-if="message.source === 'OFFICE' && message.messageType === 'ROUTE'">-->
<!--            <span class="message-header">Route</span>-->
<!--            <span class="message-detail">&#9758;</span>-->
<!--          </div>-->
<!--          <div v-if="message.source === 'OFFICE' && message.messageType === 'STATE'">-->
<!--            <span class="message-header">Status</span>-->
<!--            <span class="message-detail">?</span>-->
<!--          </div>-->
<!--          <div v-if="message.source === 'PLANE'">-->
<!--            <span class="message-header">{{ message.planeName }} - &#9992;</span>-->
<!--          </div>-->
<!--          <div class="message-date">{{ message.timestamp }}</div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

<!--    <div class="radar">-->
<!--      <div style="padding: 10px; text-align: center; color: red; font-size: 200%">-->
<!--        <span v-for="(route, index) in tempRoute">-->
<!--          {{ route }} <b v-if="index !== tempRoute.length - 1">&rtri;</b>-->
<!--        </span>-->
<!--        <button @click="submitRoute" v-if="tempRoute.length > 1" class="roundButton">&check;</button>-->
<!--        <button @click="cancelRoute" v-if="tempRoute.length > 0" class="roundButton">&cross;</button>-->
<!--      </div>-->
<!--      <Airport v-for="airport in airports" :key="airport.name" :airport="airport" :click-callback="addRoute"/>-->
<!--      <div class="planeArea">-->
<!--        <Plane v-for="(row, index) in planes" :key="index" :level="index" :plane="row"></Plane>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->


<!--<script>-->
<!--import axios from 'axios';-->
<!--import Plane from "../components/Plane.vue";-->
<!--import airport from "../components/Airport.vue";-->
<!--import plane from "../components/Plane.vue";-->

<!--export default {-->
<!--  computed: {-->
<!--    plane() {-->
<!--      return plane-->
<!--    },-->
<!--    airport() {-->
<!--      return airport-->
<!--    }-->
<!--  },-->
<!--  components: { Plane },-->
<!--  data() {-->
<!--    return {-->
<!--      socket: null,-->
<!--      airports: [],-->
<!--      tempRoute: [],-->
<!--      planes: [],-->
<!--      messages: []-->
<!--    }-->
<!--  },-->

<!--  mounted() {-->
<!--    this.defineSocket();-->
<!--    setInterval(this.wakeSocketUp, 500);-->
<!--  },-->

<!--  methods: {-->
<!--    defineSocket() {-->
<!--      this.socket = new WebSocket("ws://localhost:8081/websocket");-->

<!--      this.socket.onopen = () => {-->
<!--        console.log("WebSocket connected");-->

<!--        this.socket.onmessage = (_message) => {-->
<!--          let messageAsJson = JSON.parse(_message.data);-->

<!--          //add time for each message-->
<!--          messageAsJson.timestamp = new Date().toLocaleString();-->

<!--          if (messageAsJson.source === "AIRPORT") {-->
<!--            messageAsJson.airportName = messageAsJson.airport.name;-->
<!--            this.setAirport(messageAsJson.airport);-->
<!--          }-->

<!--          if (messageAsJson.source === "PLANE" && messageAsJson.messageType === "STATE") {-->
<!--            messageAsJson.planeName = messageAsJson.plane.name;-->
<!--            this.setPlane(messageAsJson.plane);-->
<!--          }-->

<!--          this.messages.unshift(messageAsJson);-->

<!--          if (this.messages.length > 14) {-->
<!--            this.messages.splice(14);-->
<!--          }-->
<!--        };-->
<!--      };-->

<!--      this.socket.onclose = () => {-->
<!--        console.log("WebSocket connection closed, attempting to reconnect...");-->
<!--        setTimeout(this.defineSocket, 500);-->
<!--      };-->

<!--      this.socket.onerror = (error) => {-->
<!--        console.error("WebSocket error:", error);-->
<!--        this.socket.close();-->
<!--      };-->
<!--    },-->

<!--    wakeSocketUp() {-->
<!--      if (this.socket && this.socket.readyState === WebSocket.OPEN) {-->
<!--        if (this.airports.length === 0) {-->
<!--          this.socket.send("update");-->
<!--        }-->
<!--      } else {-->
<!--        this.defineSocket();-->
<!--      }-->
<!--    },-->

<!--    setAirport(airport) {-->
<!--      let index = -1;-->
<!--      this.airports.forEach((row, i) => {-->
<!--        if (row.name === airport.name) {-->
<!--          index = i;-->
<!--        }-->
<!--      });-->

<!--      if (index >= 0) {-->
<!--        this.airports.splice(index, 1);-->
<!--      }-->

<!--      this.airports.push(airport);-->
<!--    },-->

<!--    addRoute(airport) {-->
<!--      let lastAirport = null;-->

<!--      if (this.tempRoute.length > 0) {-->
<!--        lastAirport = this.tempRoute[this.tempRoute.length - 1];-->
<!--      }-->

<!--      if (lastAirport != null && lastAirport === airport.name) {-->
<!--        return;-->
<!--      }-->

<!--      this.tempRoute.push(airport.name);-->
<!--    },-->

<!--    submitRoute() {-->
<!--      axios.post("/api/routes/route", this.tempRoute);-->
<!--      this.tempRoute = [];-->
<!--    },-->

<!--    cancelRoute() {-->
<!--      this.tempRoute = [];-->
<!--    },-->

<!--    setPlane(plane) {-->
<!--      let existsIndex = -1;-->
<!--      this.planes.forEach((row, i) => {-->
<!--        if (row.name === plane.name) {-->
<!--          existsIndex = i;-->
<!--        }-->
<!--      });-->

<!--      if (existsIndex >= 0) {-->
<!--        this.planes.splice(existsIndex, 1);-->
<!--      }-->

<!--      if (!plane.flying) {-->
<!--        return;-->
<!--      }-->

<!--      this.planes.push(plane);-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style>-->
<!--.message-header {-->
<!--  font-size: 14px;-->
<!--  font-weight: bold;-->
<!--}-->

<!--.message-detail {-->
<!--  font-size: 12px;-->
<!--  color: #777;-->
<!--}-->

<!--.message-date {-->
<!--  font-size: 10px;-->
<!--  color: gray;-->
<!--  text-align: center;-->
<!--}-->

<!--.messages {-->
<!--  padding: 10px;-->
<!--  width: 8%;-->
<!--  background: burlywood;-->
<!--}-->

<!--.radar {-->
<!--  width: 100%;-->
<!--  height: 100vh;-->
<!--  position: relative;-->
<!--  background: url("assets/field.png") no-repeat center center;-->
<!--  background-size: cover;-->
<!--}-->

<!--.roundButton {-->
<!--  padding: 4px 8px;-->
<!--  border-bottom: wheat 2px solid;-->
<!--  border-radius: 4px;-->
<!--  background: wheat;-->
<!--  color: blue;-->
<!--  cursor: pointer;-->
<!--  opacity: .8;-->
<!--}-->

<!--.roundButton:hover {-->
<!--  opacity: 1;-->
<!--}-->

<!--.planeArea {-->
<!--  position: absolute;-->
<!--  right: 0;-->
<!--  left: 0;-->
<!--  top: 0;-->
<!--  bottom: 0;-->
<!--  pointer-events: none;-->
<!--}-->

<!--.message {-->
<!--  border-radius: 10px;-->
<!--  background: white;-->
<!--  line-height: 20px;-->
<!--  color: #232323;-->
<!--  font-size: 12px;-->
<!--  margin: 10px auto;-->
<!--  padding: 5px;-->
<!--  text-align: left;-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  justify-content: space-between;-->
<!--  width: 95%;-->
<!--  max-width: 350px;-->
<!--}-->
<!--</style>-->








<!--<template>-->
<!--  <div style="width: 100%; min-height: 100vh; display: inline-flex">-->
<!--    <div class="messages">-->
<!--      <div v-for="(message, index) in messages" :key="index" :title="JSON.stringify(message)">-->
<!--        <div class="message" v-if="message.source === 'AIRPORT'">A</div>-->
<!--        <div class="message" v-if="message.source === 'OFFICE' && message.messageType === 'ROUTE'">&#9758;</div>-->
<!--        <div class="message" v-if="message.source === 'OFFICE' && message.messageType === 'STATE'">?</div>-->
<!--        <div class="message" v-if="message.source === 'PLANE'">&#9992;</div>-->
<!--      </div>-->
<!--    </div>-->

<!--    <div class="radar">-->
<!--      <div style="padding: 10px; text-align: center; color: white; font-size: 200%">-->
<!--        <span v-for="(route, index) in tempRoute">-->
<!--          {{ route }} <b v-if="index !== tempRoute.length - 1">&rtri;</b>-->
<!--        </span>-->
<!--        <button @click="submitRoute" v-if="tempRoute.length > 1" class="roundButton">&check;</button>-->
<!--        <button @click="cancelRoute" v-if="tempRoute.length > 0" class="roundButton">&cross;</button>-->
<!--      </div>-->
<!--      <Airport v-for="airport in airports" :key="airport.name" :airport="airport" :click-callback="addRoute"/>-->
<!--      <div class="planeArea">-->
<!--        <Plane v-for="(row, index) in planes" :key="index" :level="index" :plane="row"></Plane>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->

<!--import axios from 'axios';-->
<!--import Plane from "../components/Plane.vue";-->
<!--import airport from "../components/Airport.vue";-->
<!--import plane from "../components/Plane.vue";-->

<!--export default {-->
<!--  computed: {-->
<!--    plane() {-->
<!--      return plane-->
<!--    },-->
<!--    airport() {-->
<!--      return airport-->
<!--    }-->
<!--  },-->
<!--  components: {Plane},-->
<!--  data() {-->
<!--    return {-->
<!--      socket: null,-->
<!--      airports: [],-->
<!--      tempRoute: [],-->
<!--      planes: [],-->
<!--      messages: []-->
<!--    }-->
<!--  },-->

<!--  mounted() {-->
<!--    this.defineSocket()-->
<!--    setInterval(this.wakeSocketUp, 500)-->
<!--  },-->

<!--  methods: {-->

<!--    defineSocket() {-->
<!--      this.socket = new WebSocket("ws://localhost:8081/websocket");-->

<!--      this.socket.onopen = () => {-->
<!--        console.log("WebSocket connected");-->

<!--        this.socket.onmessage = (_message) => {-->
<!--          let messageAsJson = JSON.parse(_message.data);-->

<!--          if (messageAsJson.source === "AIRPORT") {-->
<!--            this.setAirport(messageAsJson.airport);-->
<!--          }-->

<!--          if (messageAsJson.source === "PLANE" && messageAsJson.messageType === "STATE") {-->
<!--            this.setPlane(messageAsJson.plane);-->
<!--          }-->

<!--          this.messages.unshift(messageAsJson);-->

<!--          if (this.messages.length > 10) {-->
<!--            this.messages.splice(10);-->
<!--          }-->
<!--        };-->
<!--      };-->

<!--      this.socket.onclose = () => {-->
<!--        console.log("WebSocket connection closed, attempting to reconnect...");-->
<!--        setTimeout(this.defineSocket, 500);-->
<!--      };-->

<!--      this.socket.onerror = (error) => {-->
<!--        console.error("WebSocket error:", error);-->
<!--        this.socket.close();-->
<!--      };-->
<!--    },-->


<!--    wakeSocketUp() {-->
<!--      if (this.socket && this.socket.readyState === WebSocket.OPEN) {-->
<!--        if (this.airports.length === 0) {-->
<!--          this.socket.send("update")-->
<!--        }-->
<!--      } else {-->
<!--        this.defineSocket()-->
<!--      }-->
<!--    },-->

<!--    setAirport(airport) {-->
<!--      let index = -1;-->
<!--      this.airports.forEach((row, i) => {-->
<!--        if (row.name === airport.name) {-->
<!--          index = i;-->
<!--        }-->
<!--      })-->

<!--      if (index >= 0) {-->
<!--        this.airports.splice(index, 1)-->
<!--      }-->

<!--      this.airports.push(airport)-->
<!--    },-->

<!--    addRoute(airport) {-->
<!--      let lastAirport = null-->

<!--      if (this.tempRoute.length > 0) {-->
<!--        lastAirport = this.tempRoute[this.tempRoute.length - 1]-->
<!--      }-->

<!--      if (lastAirport != null && lastAirport === airport.name) {-->
<!--        return-->
<!--      }-->

<!--      this.tempRoute.push(airport.name)-->
<!--    },-->

<!--    submitRoute() {-->
<!--      axios.post("/api/routes/route", this.tempRoute)-->
<!--      this.tempRoute = []-->
<!--    },-->

<!--    cancelRoute() {-->
<!--      this.tempRoute = []-->
<!--    },-->

<!--    setPlane(plane) {-->
<!--      let existsIndex = -1-->
<!--      this.planes.forEach((row, i) => {-->
<!--        if (row.name === plane.name) {-->
<!--          existsIndex = i-->
<!--        }-->
<!--      })-->

<!--      if (existsIndex >= 0) {-->
<!--        this.planes.splice(existsIndex, 1)-->
<!--      }-->

<!--      if (!plane.flying) {-->
<!--        return-->
<!--      }-->

<!--      this.planes.push(plane)-->
<!--    }-->

<!--  }-->
<!--}-->
<!--</script>-->

<!--<style>-->
<!--* {-->
<!--  margin: 0;-->
<!--  padding: 0;-->
<!--  font-family: Arial, serif;-->
<!--  color: red;-->
<!--}-->

<!--.messages {-->
<!--  padding: 10px;-->
<!--  width: 10%;-->
<!--  background: burlywood;-->
<!--}-->

<!--.radar {-->
<!--  width: 100%;-->
<!--  height: 100vh;-->
<!--  position: relative;-->
<!--  background: url("assets/field.png") no-repeat center center;-->
<!--  background-size: cover;-->
<!--}-->

<!--.roundButton {-->
<!--  padding: 4px 8px;-->
<!--  border-bottom: wheat 2px solid;-->
<!--  border-radius: 4px;-->
<!--  background: wheat;-->
<!--  color: blue;-->
<!--  cursor: pointer;-->
<!--  opacity: .8;-->
<!--}-->

<!--.roundButton:hover {-->
<!--  opacity: 1;-->
<!--}-->

<!--.planeArea {-->
<!--  position: absolute;-->
<!--  right: 0;-->
<!--  left: 0;-->
<!--  top: 0;-->
<!--  bottom: 0;-->
<!--  pointer-events: none;-->
<!--}-->

<!--.message {-->
<!--  border-radius: 10px;-->
<!--  background: white;-->
<!--  line-height: 40px;-->
<!--  color: #232323;-->
<!--  font-size: 30px;-->
<!--  width: 50px;-->
<!--  margin: 10px auto;-->
<!--  text-align: center;-->
<!--}-->

<!--</style>-->
