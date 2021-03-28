<template>
    <div>
        <sidebar></sidebar>
        <div id="main-content">
            <div v-if="this.check_logout" >
                <md-button style="font-size: 18px;font-family: 'Noto Sans KR', sans-serif;margin-left: 92%" v-on:click="this.logout">
                    <i class="fa fa-sign-out-alt"></i> 로그아웃
                </md-button>
            </div>
            <div style="margin-left : 10px;margin-right: 10px; margin-top:10px">
                <router-view></router-view>
            </div>
        </div>
    </div>
</template>

<script>
    import Sidebar from './Sidebar'
    import store from '../store/index'
    import router from '../router';
    export default {
        name: "layout",
        components:{
            Sidebar
        },
        methods:{
            logout:function () {
                router.push('/logout');
            }
        },
        computed:{
            check_logout : function () {
                console.log(store.state.userId + "gkgk")

                if(store.state.userId != null){
                    return true
                }
                else{
                    return false
                }
            }
        }
    }
</script>

<style scoped>
    #main-content{
        margin-left: 180px;
    }

</style>