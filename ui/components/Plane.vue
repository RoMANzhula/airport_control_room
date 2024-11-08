<template>
  <div class="planeBlock" :style="'z-index:' + level">
    <div class="icon" :style="'left: ' + plane.xCoordinate + '%; top: ' + plane.yCoordinate + '%; transform: rotate(' + (plane.angleOfBoard - 180) + 'deg);'">&#9992;</div>
    <div class="title" :style="'left: ' + plane.xCoordinate + '%; top: ' + plane.yCoordinate + '%;'">{{plane.name}}</div>
  </div>
</template>

<script>
  export default {
    props: {
      level: 0,
      plane: null
    }
  }
</script>

<style>
.planeBlock {
  width: 100%;
  min-height: 100vh;
  position: absolute;
}

.planeBlock .icon {
  font-size: 300%;
  color: yellow;
  transform-origin: center center;
  position: absolute;
}

.planeBlock .title {
  background: transparent;
  color: blue;
  padding: 3px 6px;
  text-align: center;
  position: absolute;
}

</style>
