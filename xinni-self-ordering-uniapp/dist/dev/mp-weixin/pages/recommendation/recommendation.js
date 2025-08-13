"use strict";
const common_vendor = require("../../common/vendor.js");
const api_recommendation = require("../../api/recommendation.js");
require("../../utils/http.js");
require("../../stores/modules/user.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "recommendation",
  setup(__props) {
    const userQuery = common_vendor.ref("");
    const messages = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const scrollTop = common_vendor.ref(0);
    common_vendor.onMounted(() => {
      loadChatHistory();
    });
    const loadChatHistory = () => {
      try {
        const savedData = common_vendor.index.getStorageSync("chatHistory");
        if (savedData) {
          const chatHistory = savedData;
          const now = Date.now();
          const sevenDaysInMillis = 7 * 24 * 60 * 60 * 1e3;
          if (now - chatHistory.timestamp < sevenDaysInMillis) {
            messages.value = chatHistory.messages;
            return;
          } else {
            common_vendor.index.removeStorageSync("chatHistory");
          }
        }
        messages.value = [
          {
            type: "ai",
            content: "您好！我是您的专属菜品推荐助手，请告诉我您的用餐需求，例如口味偏好、用餐人数、场合等，我会为您推荐合适的菜品。"
          }
        ];
      } catch (error) {
        console.error("加载聊天历史失败:", error);
        messages.value = [
          {
            type: "ai",
            content: "您好！我是您的专属菜品推荐助手，请告诉我您的用餐需求，例如口味偏好、用餐人数、场合等，我会为您推荐合适的菜品。"
          }
        ];
      }
    };
    const saveChatHistory = () => {
      try {
        const chatHistory = {
          messages: messages.value,
          timestamp: Date.now()
        };
        common_vendor.index.setStorageSync("chatHistory", chatHistory);
      } catch (error) {
        console.error("保存聊天历史失败:", error);
      }
    };
    const sendMessage = async () => {
      if (!userQuery.value.trim()) {
        common_vendor.index.showToast({
          title: "请输入用餐需求",
          icon: "none"
        });
        return;
      }
      messages.value.push({
        type: "user",
        content: userQuery.value
      });
      saveChatHistory();
      const query = userQuery.value;
      userQuery.value = "";
      await scrollToBottom();
      try {
        loading.value = true;
        const res = await api_recommendation.getDishRecommendationAPI(query);
        messages.value.push({
          type: "ai",
          content: res.data
        });
        saveChatHistory();
      } catch (error) {
        messages.value.push({
          type: "ai",
          content: "抱歉，获取推荐失败，请稍后再试~"
        });
        saveChatHistory();
        console.error(error);
      } finally {
        loading.value = false;
        scrollToBottom();
      }
    };
    const formatAiMessage = (content) => {
      if (!content)
        return "";
      return content.replace(/\n/g, "<br>").replace(/### (.*?)(<br>|$)/g, '<h3 style="margin: 10px 0 5px; font-size: 16px; color: #333;">$1</h3>').replace(/\*\*([^*]+?)\*\*/g, "<strong>$1</strong>").replace(/\* ([^<]*?)(<br>|$)/g, '<view style="display: flex; align-items: flex-start; margin: 5px 0;"><view style="width: 4px; height: 4px; border-radius: 50%; background: #666; margin: 8px 8px 0 0;"></view><view style="flex: 1;">$1</view></view>$2');
    };
    const scrollToBottom = async () => {
      await common_vendor.nextTick$1();
      scrollTop.value = 999999;
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(messages.value, (message, index, i0) => {
          return common_vendor.e({
            a: message.type === "user"
          }, message.type === "user" ? {} : {}, {
            b: message.type === "ai"
          }, message.type === "ai" ? {
            c: formatAiMessage(message.content)
          } : {
            d: common_vendor.t(message.content)
          }, {
            e: index,
            f: common_vendor.n(message.type)
          });
        }),
        b: loading.value
      }, loading.value ? {} : {}, {
        c: scrollTop.value,
        d: common_vendor.o(sendMessage),
        e: userQuery.value,
        f: common_vendor.o(($event) => userQuery.value = $event.detail.value),
        g: common_vendor.o(sendMessage),
        h: loading.value
      });
    };
  }
});
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1100a80a"], ["__file", "D:/xinni-self-ordering-main/xinni-self-ordering/xinni-self-ordering-uniapp/src/pages/recommendation/recommendation.vue"]]);
wx.createPage(MiniProgramPage);
