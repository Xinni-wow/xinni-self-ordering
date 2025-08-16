"use strict";
const common_vendor = require("../common/vendor.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "RecommendationAI",
  setup(__props) {
    const right = common_vendor.ref(30);
    const bottom = common_vendor.ref(150);
    const isDragging = common_vendor.ref(false);
    const sysInfo = common_vendor.index.getSystemInfoSync();
    const windowWidth = common_vendor.ref(sysInfo.windowWidth);
    const windowHeight = common_vendor.ref(sysInfo.windowHeight);
    let startX = 0;
    let startY = 0;
    let startRight = 0;
    let startBottom = 0;
    const handleTouchStart = (e) => {
      isDragging.value = true;
      const touch = e.touches[0];
      startX = touch.clientX;
      startY = touch.clientY;
      startRight = right.value;
      startBottom = bottom.value;
    };
    const handleTouchMove = (e) => {
      if (!isDragging.value)
        return;
      const touch = e.touches[0];
      const deltaX = touch.clientX - startX;
      const deltaY = touch.clientY - startY;
      let newRight = startRight - deltaX;
      let newBottom = startBottom - deltaY;
      newRight = Math.max(5, Math.min(newRight, windowWidth.value - 55));
      newBottom = Math.max(5, Math.min(newBottom, windowHeight.value - 55));
      right.value = newRight;
      bottom.value = newBottom;
    };
    const handleTouchEnd = () => {
      isDragging.value = false;
      const sphereCenterX = right.value + 25;
      if (sphereCenterX < windowWidth.value / 2) {
        right.value = 15;
      } else {
        right.value = windowWidth.value - 55;
      }
      bottom.value = Math.max(5, Math.min(bottom.value, windowHeight.value - 55));
    };
    const navigateToRecommendation = () => {
      if (!isDragging.value) {
        common_vendor.index.navigateTo({
          url: "/pages/recommendation/recommendation"
        });
      }
    };
    return (_ctx, _cache) => {
      return {
        a: isDragging.value ? 1 : "",
        b: right.value + "px",
        c: bottom.value + "px",
        d: common_vendor.o(handleTouchStart),
        e: common_vendor.o(handleTouchMove),
        f: common_vendor.o(handleTouchEnd),
        g: common_vendor.o(navigateToRecommendation)
      };
    };
  }
});
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-a20ca00a"], ["__file", "D:/xinni-self-ordering-main/xinni-self-ordering/xinni-self-ordering-uniapp/src/components/RecommendationAI.vue"]]);
wx.createComponent(Component);
