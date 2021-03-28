<template>
    <div id="container">
        <div class="centered-container">
            <md-content class="md-elevation-3">
                <div class="title">
                    <!--<img src='../assets/img/ichat_logo.png'>-->
                    <div class="md-title">관리자 로그인</div>
                </div>

                <div class="form">
                    <md-field>
                        <label>관리자 ID </label>
                        <md-input id="idInput" v-model="userId" autofocus></md-input>
                    </md-field>

                    <md-field>
                        <label>Password </label>
                        <md-input id="pwInput" v-model="password" type="password" @keyup.enter="login"></md-input>
                    </md-field>
                </div>

                <div class="actions md-layout md-alignment-center-space-between">
                    <md-button class="md-primary " @click="showPasswordChange=true">패스워드 변경</md-button>
                    <md-button class="md-primary md-raised" @click="login">로그인</md-button>
                </div>

            </md-content>
            <div class="background"/>
        </div>

        <md-dialog class="changeSentence" :md-active.sync="showPasswordChange">
            <md-dialog-title>패스워드변경</md-dialog-title>
            <div class="md-layout md-gutter md-alignment-center-left">
                <div class="md-layout-item md-size-10">
                </div>
                <div class="md-layout-item">
                    <md-field>
                        <label>현재 패스워드</label>
                        <md-input v-model="password" type="password"></md-input>
                    </md-field>
                </div>
                <div class="md-layout-item md-size-10">
                </div>
            </div>
            <div class="md-layout md-gutter md-alignment-center-left">
                <div class="md-layout-item md-size-10">
                </div>
                <div class="md-layout-item">
                    <md-field>
                        <label>변경할 패스워드</label>
                        <md-input v-model="newPassword" type="password"></md-input>
                    </md-field>
                </div>
                <div class="md-layout-item md-size-10">
                </div>

            </div>
            <div class="md-layout md-gutter md-alignment-center-left">
                <div class="md-layout-item md-size-10">
                </div>
                <div class="md-layout-item">
                    <md-field>
                        <label>변경할 패스워드 확인</label>
                        <md-input v-model="passwordConfirm" type="password"></md-input>
                    </md-field>
                </div>
                <div class="md-layout-item md-size-10">
                </div>
            </div>
            <div class="md-layout md-alignment-center-center">
                <md-button class="md-primary md-raised" @click="changePassword">패스워드 변경</md-button>
                <md-button class="md-raised" @click="showPasswordChange=false">취소</md-button>
            </div>

        </md-dialog>
    </div>

</template>

<script>
    import axios from 'axios';
    import store from '../store';
    import CryptoJS from 'crypto-js';
    import router from '../router';





    var data_list = {
        password: '',
        newPassword: '',
        passwordConfirm : '',
        userId: '',
        showPasswordChange: false,
        jwt : require('jsonwebtoken')
    };

    export default {

        name : 'Login',

        data : function(){
            return data_list
        },

        mounted : function () {
            this.password = '';
            store.commit('getMode');
            console.log("login page");
            console.log("state call",store.state.baseURL);
        },

        methods: {
            login : function () {
                if(this.userId.length===0 || this.password.length===0){
                    alert("관리자 ID 및 비밀번호를 입력하세요.");
                    return;
                }
                let encodedPassword = ''
                if (this.password !== '') {
                    encodedPassword = CryptoJS.SHA256(this.password).toString();
                }

                axios({
                    method : 'POST',
                    url : store.state.baseURL + '/user/login',
                    auth : {
                        username: this.userId,
                        password: encodedPassword,
                    }
                }).then((response) => {
                    const token = response.data;
                    // see https://www.npmjs.com/package/jsonwebtoken
                    /*const decoded = this.jwt.decode(token, {complete: true});

                    if (decoded.payload.level == 0) {
                        if (this.password === "admin#12") {
                            alert('최초 로그인시는 패스워드를 변경해야 합니다.');
                            this.showPasswordChange = true;
                        } else {
                            store.commit('setToken', token);
                            router.push('/');
                        }
                    } else {
                        alert('관리자 권한이 없습니다.');
                    }*/

                    if (this.password === "admin#12") {
                        alert('최초 로그인시는 패스워드를 변경해야 합니다.');
                        this.showPasswordChange = true;
                    } else {
                        store.commit('setToken', token);
                        router.push('/');
                    }
                })
                    .catch((error) => {
                        alert(error.response.data);
                    })
            },
            changePassword: function () {
                let encodedPassword = ""

                if (this.password !== "") {
                    encodedPassword = CryptoJS.SHA256(this.password).toString();
                }

                var pwPattern = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])([a-zA-Z0-9]).{8,}$/;

                if (pwPattern.test(this.newPassword) == false) {

                    alert("비밀번호는 영문(대소문자 구분), 숫자, 특수문자 조합, 8자리 이상으로 만들어 주시기 바랍니다.")
                    return false;
                }

                if (this.newPassword !== this.passwordConfirm) {

                    alert("변경할 패스워드가 일치하지 않습니다. ")
                    return false;
                }
                const newEncodedPassword = CryptoJS.SHA256(this.newPassword).toString();

                const payload = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(JSON.stringify({
                        userId: this.userId,
                        password: encodedPassword,
                        newPassword: newEncodedPassword,
                    },)
                ));

                axios({
                    method : 'PUT',
                    url : store.state.baseURL + '/user/changePassword',
                    headers: {'Authorization': 'Bearer ' + payload}
                }).then((response) => {
                    if (response.data === 'OK') {
                        alert('패스워드가 변경되었습니다. 다시 로그인해주세요');
                        this.showPasswordChange = false;
                        router.push('/');
                    }
                })
                    .catch((error)=>{
                        alert(error.response.data);
                    })
            }
        }

    }
</script>


<style lang="scss">
    .centered-container {
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
        height: 50vh;

        .title {
            text-align: center;
            margin-bottom: 30px;

            img {
                margin-bottom: 16px;
                max-width: 160px;
            }
        }

        .actions {
            .md-button {
                margin: 0;
            }
        }

        .form {
            margin-bottom: 60px;
        }

        .md-dialog {
            margin-bottom: 20px;
        }

        .background {
            position: absolute;
            height: 100%;
            width: 100%;
            top: 0;
            bottom: 0;
            right: 0;
            left: 0;
            z-index: 0;
        }

        .md-content {
            z-index: 1;
            padding: 40px;
            width: 100%;
            max-width: 400px;
            position: relative;
        }

        .loading-overlay {
            z-index: 10;
            top: 0;
            left: 0;
            right: 0;
            position: absolute;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }
</style>
