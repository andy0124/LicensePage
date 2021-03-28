<template>
    <sidebar-menu :menu="menu" :hide-toggle="true" :collapsed="false" :disable-hover="false" :width='"180px"' ></sidebar-menu>
</template>

<script>
    import Vue from 'vue'
    import VueSidebarMenu from 'vue-sidebar-menu'
    /*import 'vue-sidebar-menu/dist/vue-sidebar-menu.css'*/
    import '../assets/scss/vue-sidebar-menu.scss'
    Vue.use(VueSidebarMenu)

    import '@fortawesome/fontawesome-free/css/all.css'
    import '@fortawesome/fontawesome-free/js/all.js';

    export default {
        name: "Sidebar",
        data(){
            return{
                menu:[
                    {
                        header: true,
                        title: '제품라이센스관리'
                    },
                    {
                        href: '/submit',
                        title: '라이센스 발급',
                        icon: 'fa fa-key'
                    },
                    {
                        href: '/read',
                        title: '발급 기록 조회',
                        icon: 'fa fa-search'
                    },
                    {
                        href: '/users',
                        title: '사용자 관리',
                        icon: 'fa fa-user'
                    },
                    /*{
                        href: '/logout',
                        title: '로그아웃',
                        icon: 'fa fa-sign-out',
                    }*/
                ]
            }
        }


    }
</script>

<style scoped>

</style>