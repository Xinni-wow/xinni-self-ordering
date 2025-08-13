import { http } from '@/utils/http'

/**
 * 根据用户需求推荐菜品
 */
export const getDishRecommendationAPI = (query: string) => {
  return http<string>({
    method: 'GET',
    url: `/user/recommendation/dish`,
    data: {
      query
    },
    timeout: 60000 // 增加超时时间到60秒
  })
}