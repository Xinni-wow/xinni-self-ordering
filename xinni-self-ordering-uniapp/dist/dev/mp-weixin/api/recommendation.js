"use strict";
const utils_http = require("../utils/http.js");
const getDishRecommendationAPI = (query) => {
  return utils_http.http({
    method: "GET",
    url: `/user/recommendation/dish`,
    data: {
      query
    },
    timeout: 6e4
    // 增加超时时间到60秒
  });
};
exports.getDishRecommendationAPI = getDishRecommendationAPI;
