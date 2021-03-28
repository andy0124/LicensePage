<template>
    <div class="content">
        <div id="vueapp" class="vue-app">
            <div>
                <kendo-grid ref='grid' :data-source="kendoDataSource"
                            :editable="{confirmation: '삭제 후 복구는 불가능합니다. \n선택한 항목을 삭제 하시겠습니까?', mode:'inline'}"
                            :toolbar="['create']"
                            :resizable='true'
                            :sortable='true'
                            :editable-confirmation='false'
                            :messages-commands-create="'새로만들기'"
                            :messages-commands-edit="'편집'"
                            :messages-commands-destroy="'삭제'"
                            :messages-commands-save="'편집내용 저장'"
                            :messages-commands-cancel="'취소'"
                            :messages-commands-canceledit="'취소'"
                            :messages-commands-update="'확인'"
                            :column-menu-messages-sort-ascending="'오름차순'"
                >

                    <kendo-grid-column :field="'id'" :editable='isEditable' :title="'사용자id'"
                                       :width="100"></kendo-grid-column>
                    <kendo-grid-column :field="'name'" :title="'이름'" :width="120"></kendo-grid-column>
                    <!--<kendo-grid-column :field="'isAdmin'" :title="'관리자여부'" :width="50"></kendo-grid-column>-->
                    <kendo-grid-column :field="'password'" :title="'패스워드'" :width="100"></kendo-grid-column>
                    <kendo-grid-column :command="['edit','destroy']"
                                       :title="'편집/삭제'"
                                       :width="90"></kendo-grid-column>
                    <kendo-grid-column :command="{ text: '패스워드설정', click: onSetPassword }"
                                       :title="'패스워드설정'"
                                       :width="90"></kendo-grid-column>

                </kendo-grid>
            </div>


            <md-dialog class="setPassword" :md-active.sync="showPasswordChange" :md-backdrop=true>
                <md-dialog-title>패스워드 설정</md-dialog-title>

                <div class="md-layout md-gutter md-alignment-center-left">
                    <div class="md-layout-item md-size-10"></div>

                    <div class="md-layout-item">
                        <md-field>
                            <label>설정할 패스워드</label>
                            <md-input v-model="newPassword" type="password"></md-input>
                        </md-field>
                    </div>
                    <div class="md-layout-item md-size-10">
                    </div>

                </div>
                <div class="md-layout md-gutter md-alignment-center-left">
                    <div class="md-layout-item md-size-10"></div>

                    <div class="md-layout-item">
                        <md-field>
                            <label>설정할 패스워드 확인</label>
                            <md-input v-model="newPasswordConfirm" type="password"></md-input>
                        </md-field>
                    </div>
                    <div class="md-layout-item md-size-10">
                    </div>

                </div>
                <div class="md-layout md-alignment-center-center">
                    <md-button class="md-primary md-raised" @click="setPassword">패스워드 설정</md-button>
                    <md-button class="md-raised" @click="showPasswordChange=false">취소</md-button>
                </div>

            </md-dialog>
        </div>
        <p></p>

    </div>
</template>


<script>
    import $ from 'jquery';
    import axios from 'axios';
    import CryptoJS from 'crypto-js';
    import store from '../store';
    import Vue from 'vue';
    import kendo from '@progress/kendo-ui';
    import '@progress/kendo-theme-default/dist/all.css';
    import {GridInstaller} from '@progress/kendo-grid-vue-wrapper';

    import VueMaterial from 'vue-material'
    import 'vue-material/dist/vue-material.min.css';
    import 'vue-material/dist/theme/default.css'

    Vue.use(GridInstaller);
    Vue.use(VueMaterial);


    var data_list = {
        userId: '',
        curPassword: '',
        newPassword: '',
        newPasswordConfirm : '',
        showPasswordChange: false,
        flag : false,
        kendoDataSource : new kendo.data.DataSource()
    };

    export default {

        name : 'Users',

        data : function(){
            return data_list
        },

        mounted : function(){
            this.initKendoDataSource('');
        },

        methods : {
            beforeEdit : function (event) {
                console.log(event);
            },
            onSetPassword : function (event) {
                event.preventDefault();
                const grid = (this.$refs.grid).kendoWidget();
                const userInfo = (grid.dataItem($(event.currentTarget).closest("tr")));

                this.userId = userInfo.id;
                this.curPassword = userInfo.password;
                this.showPasswordChange = true;
            },
            setPassword : function () {
                var pwPattern = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])([a-zA-Z0-9]).{8,}$/;

                if (pwPattern.test(this.newPassword) == false) {

                    alert("비밀번호는 영문(대소문자 구분), 숫자, 특수문자 조합, 8자리 이상으로 만들어 주시기 바랍니다.")
                    return false;
                }

                if (this.newPassword != this.newPasswordConfirm){
                    alert("변경할 패스워드가 서로 일치하지 않습니다.")
                    return false;
                }

                const encodedPassword = CryptoJS.SHA256(this.newPassword).toString();

                const payload = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(JSON.stringify({
                        userId: this.userId,
                        password: encodedPassword,
                    },)
                ));

                const apiKey = 'f7d8ce90-51ec-4c1c-bbfb-e33307e873da';

                axios({
                    method : 'PUT',
                    url : store.state.baseURL + '/user/setPassword?apiKey=' + apiKey,
                    headers: {'Authorization': 'Bearer ' + payload}
                }).then((response) => {
                    if (response.data === 'OK') {
                        console.log("check")
                        this.showPasswordChange = false;
                        this.kendoDataSource.read();
                    }
                })
                    .catch((error)=>{
                        alert(error.response.data);
                    })
            },
            isEditable : function (event) {
                if (event.id === null) {
                    return true;
                } else {
                    return false;
                }
            },
            initKendoDataSource : function (baseURL) {
                /*const self = this;*/
                this.kendoDataSource = new kendo.data.DataSource({

                    transport: {

                        read: (options) => {
                            const apiKey = 'f7d8ce90-51ec-4c1c-bbfb-e33307e873da';
                            axios.get(baseURL + '/user?apiKey=' + apiKey, {})
                                .then((response) => {
                                    console.log("response");
                                    console.log(response);
                                    options.success(response.data);
                                }).catch((error) => {
                                alert(error.response.data);
                                options.error(error);
                            });
                        },
                        update: (options) => {
                            console.log(options);
                            var item = options.data;
                            if (item.id === null) {
                                alert("id를 입력해야 합니다.")
                                options.error();
                                return;
                            }
                            if (item.id.length > 12) {
                                alert("id는 12자 이하로 가능합니다..")
                                options.error();
                                return;
                            }

                            axios.put(baseURL + '/user/update', item)
                                .then((response) => {
                                    console.log(response);
                                    options.success(response.data);
                                })
                                .catch((error) => {
                                    alert(error.response.data);
                                    options.error(error);
                                });


                        },
                        create: (options) => {
                            var item = options.data;
                            console.log(options);
                            if (item.id === null) {
                                alert("id를 입력해야 합니다.")
                                options.error();
                                return;
                            }
                            if (item.id.length > 12) {
                                alert("id는 12자 이하로 가능합니다..")
                                options.error();
                                return;
                            }

                            axios.post(baseURL + "/user/insert", item)
                                .then((response) => {

                                    console.log(response);
                                    options.success(response.data);
                                })
                                .catch((error) => {
                                    alert(error.response.data);
                                    options.error(error);
                                });
                        },
                        destroy: (options) => {
                            const item = options.data;
                            console.log(item);
                            // delete는 이렇게 해야 넘거감
                            axios.delete(baseURL + '/user/delete', {data: item})
                                .then((response) => {
                                    console.log(response);
                                    options.success(response.data);
                                })
                                .catch((error) => {
                                        alert(error.response.data);
                                        options.error(error);
                                        this.kendoDataSource.cancelChanges()
                                        return;
                                    }
                                );
                        },
                    },

                    schema: {
                        model: {
                            id: 'id',
                            fields: {
                                id: {type: 'string', nullable: true, editable: true},
                                password: {type: 'string', editable: false},
                                name: {type: 'string', validation: {required: true}},
                                /*isAdmin: {type: 'boolean'},*/
                            },
                        },
                    }
                });
            }
        }



    }
</script>

<style scoped>
    .k-grid-toolbar a {
        float: right;
    }

    .md-dialog {
        /*width: 60%;*/
        /*height: 768px;*/
        z-index: 9;
        margin-bottom: 20px;
    }

    .md-autocomplete {
        width: 100%;
    }

    .md-textarea {
        height: 100%;
    }

    textarea {
        font-family: Arial;
        font-size: 14px
    }
</style>
