import Vue from 'vue'
import Vuex from 'vuex'
import { loginApi,logoutApi,getUserInfoApi } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/cookie'

Vue.use(Vuex)
export default new Vuex.Store({
  state: {
    userInfo: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : null,
    webSiteInfo: {
      showList:[]
    },
    token: getToken() || '',
    loginVisible: false,
    searchVisible: false,
    mobileMenuVisible: false,
    visitorAccess: 0,
    siteAccess: 0,
    isLoading: false,
    notice: null
  },
  mutations: {
    setSiteInfo(state, info) {
      state.webSiteInfo = info
    },
    SET_TOKEN(state, token) {
      state.token = token
      setToken(token)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      sessionStorage.setItem("user", JSON.stringify(userInfo))
    },
    SET_LOGIN_VISIBLE(state, visible) {
      state.loginVisible = visible
    },
    SET_SEARCH_VISIBLE(state, visible) {
      state.searchVisible = visible
    },
    SET_MOBILE_MENU_VISIBLE(state, visible) {
      state.mobileMenuVisible = visible
    },
    setMobileMenuVisible(state, value) {
      state.mobileMenuVisible = value
    },
    setVisitorAccess(state, value) {
      state.visitorAccess = value
    },
    setSiteAccess(state, value) {
      state.siteAccess = value
    },
    SET_LOADING(state, status) {
      state.isLoading = status
    },
    SET_NOTICE(state, notice) {
      state.notice = notice
    }
  },
  actions: {
    
    /**
     * 设置公告信息
     */
    setNotice({ commit }, notice) {
      commit('SET_NOTICE', notice)
    },

    /**
     * 设置站点信息
     */
    setSiteInfo({ commit }, info) {
      commit('setSiteInfo', info)
    },
    /**
     * 获取用户信息
     */
    async getUserInfo({ commit }) {
      if(getToken()){
        const res = await getUserInfoApi()
        commit('SET_USER_INFO', res.data)
      }
    },

    /**
     * 登录
     */
    async loginAction({ commit }, loginData) {
      try {
        const res = await loginApi(loginData)
        if (res.data) {
          commit('SET_TOKEN', res.data.token)
          commit('SET_USER_INFO', res.data)
          return Promise.resolve(res)
        }
       return Promise.reject(res)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    /**
     * 退出登录
     */
    async logout({ commit }) {
      await logoutApi()
      removeToken()
      commit('SET_USER_INFO', null)
    },

    showLoading({ commit }) {
      commit('SET_LOADING', true)
    },

    hideLoading({ commit }) {
      commit('SET_LOADING', false)
    }
  }
})