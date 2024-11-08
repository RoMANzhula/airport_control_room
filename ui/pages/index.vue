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

