import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);


export default new Vuex.Store({
    state: {
        // 테스트시에는 http 붙여서 테스트할 주소 및 포트입력
        baseURL: '',
        token: localStorage.getItem('token'),
        additionalLink: '',
        userId: null,
        accessLevel: null,

    },
    mutations: {
        getMode(state){
          if(process.env.NODE_ENV === 'development') {
              state.baseURL = 'http://127.0.0.1:8500';
              console.log("development");
          }
          if(process.env.NODE_ENV === 'production') {
              state.baseURL = ' ';
              console.log('production');
          }
        },
        setToken(state, token) {
            localStorage.setItem('token', token);
            state.token = localStorage.getItem('token');
        },
        getToken(state) {
            state.token = localStorage.getItem('token');
        },
        delToken(state) {
            localStorage.removeItem('token');
            state.token = null;
            // console.log(state.token );
        },

    },
    actions: {},
    modules: {},
    getters: {
        getUserIdState (state) {
            if(state.userId !== null){
                return true;
            }
            else{
                return false;
            }
        }
    }
});
