<template>
  <div class="radioBlock" :style="'position: absolute; left: calc(' + airport.x + '% - 50px); top: calc(' + airport.y + '% - 50px)'">
    <div class="radioPoint" @click="clickCallback(airport)"></div>
    <div class="title">
      {{airport.name}}
      <span v-for="(row, index) in airport.planes" style="color: #fff;" :title="row" :key="index">
        &#9992;
      </span>
    </div>
  </div>
</template>

<script>
  export default {
    props:{
      airport: null,
      clickCallback: null
    }
  }
</script>

<style>
  .radioBlock .radioPoint{
    width: 100px;
    height: 100px;
    background: transparent url("assets/airport.png") no-repeat center center/cover;
  }

  .radioBlock .title{
    background: midnightblue;
    color: greenyellow;
    padding: 2px 5px;
    text-align: center;
    border-radius: 8px;
    margin-top: -10px;
  }
</style>
