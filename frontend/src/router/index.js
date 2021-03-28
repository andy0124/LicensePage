import Vue from 'vue'
import VueRouter from 'vue-router'

//modules
import licenseSubmit from '../components/LicenseSubmit.vue'
import licenseIssuanceRecord from '../components/LicenseIssuanceRecord'
import login from '../components/Login'
import users from '../components/Users'

import Layout from '../layout/layout'

Vue.use(VueRouter)
var jwt = require('jsonwebtoken');


////////////////////////////////
import store from '../store/index'
////////////////////////////////





////////////////////////////////
function checkToken(token) {
    /**
     * 1. 토큰 있는지 체크
     * 2. 토큰 유효기간 체크
     * 3. 토큰 사용자 및 권한 store에 저장
     *
     */
    if (token === undefined) {
        return false;
    }
    const decoded = jwt.decode(token, {complete: true});
    if (decoded === null) {
        return false;
    }
    /*store.state.accessLevel = decoded.payload.level;*/
    store.state.userId = decoded.payload.userId;
    /*const issueTimestamp = decoded.payload.timestamp;*/
    const dailyTimeout = decoded.payload.dailyTimeout;
    /*const userId = decoded.payload.userId;*/
    if (Date.now() > dailyTimeout) {
        /*store.state.accessLevel = null;*/
        store.state.userId = null;
        return false;
    }

    // console.log(decoded);
    return true;
}


const requireAuth = () => (to, from, next) => {
    if (checkToken(store.state.token)) {
        return next();
    } else {
        next('/login');
    }
};
const logout = () => (to, from, next) => {
    if (confirm('로그아웃 하시겠습니까?')) {
        store.commit('delToken');
        store.state.userId = null;
        next('/login');
    }
};
////////////////////////////////

/*const routes = [
    {
        path:'/',
        component:Layout,
        redirect:'/submit',
        children: [
            {
                path:'/submit',
                name:'라이센스발급',
                component: licenseSubmit
            },
            {
                path:'/read',
                name:'라이센스발급기록조회',
                component: licenseIssuanceRecord
            },
            {
                path: '/login',
                name: '관리자 로그인',
                component: login,
                /!*meta: {
                    hideFooter: true,
                },*!/
            },
            {
                path: '/users',
                name: '사용자관리',
                component: users,
                /!*beforeEnter: requireAuth(),*!/
            }
        ]

    }
]*/

const routes = [
    {
        path:'/',
        component:Layout,
        redirect:'/submit',
        children: [
            {
                path:'/submit',
                name:'라이센스발급',
                beforeEnter: requireAuth(),
                component: licenseSubmit
            },
            {
                path:'/read',
                name:'라이센스발급기록조회',
                beforeEnter: requireAuth(),
                component: licenseIssuanceRecord
            },
            {
                path: '/login',
                name: '관리자 로그인',
                component: login,
                /*meta: {
                    hideFooter: true,
                },*/
            },
            {
                path: '/users',
                name: '사용자관리',
                beforeEnter: requireAuth(),
                component: users,
            },
            {
                path: '/logout',
                name: '로그아웃',
                beforeEnter: logout(),
            },
        ]

    }
]

const router = new VueRouter({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: routes,
});

export default router;
