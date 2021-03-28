<template>
    <div id="licens_submit">
        <div id ="information_input">
            <img src="../assets/wisenut.png" style="width: 50%; margin-top: 20px; margin-left: 20px; padding-bottom: 30px">
            <div Align="left" style="margin-left: 50px">
                <div id="temp">
                    <div>
                        <label class="titlefont" for="product_name">제품명&nbsp;</label>
                    </div>
                    <div id="product_name">
                        <select name="product_name"  v-model="product" style="width:40%; font-family: 'Noto Sans KR', sans-serif; font-size: 18px">
                            <option value=''>선택안함</option>
                            <option value="WiseiChat V3">WiseiChat V3</option>
                            <option value="iDesk V2">iDesk V2</option>
                        </select>
                    </div>
                    <div>
                        <label class = "titlefont" for="product_period" >만료일&nbsp;</label>
                    </div>
                    <div id="product_period" style="display: grid; grid-template-columns: 50% 50%">
                        <v-date-picker style="width:70%" v-model="date" :disabled-dates='{ start: null, end: permanent_check_datepicker }'  :input-props='{placeholder: "무기한"}'></v-date-picker>
                        <div>
                            <label for="checkbox">무기한</label>
                            <input type="checkbox" id="checkbox" v-model="permanent_check" @change="permanent_check_change">
                        </div>
                    </div>
                    <div >
                        <label class = "titlefont" for="regname" >발급 요청자</label>
                    </div>
                    <div>
                        <input type="text" id="regname" v-model="regName" style="width: 40% ;height: 30px; font-family: 'Noto Sans KR', sans-serif; font-size: 18px"/>
                    </div>
                    <div>
                        <label class= "titlefont" for="comp" >사이트 이름</label>
                    </div>
                    <div>
                        <input type="text" id="comp" v-model="comP" style="width: 40% ;height: 30px; font-family: 'Noto Sans KR', sans-serif; font-size: 18px"/>
                    </div>
                    <div>
                        <label class = "titlefont" for="HDid" style="width: 30px">하드웨어 아이디</label>
                    </div>
                    <div>
                        <input type="text" id="HDid" v-model="hdID" style="width: 100%; height: 30px; font-family: 'Noto Sans KR', sans-serif; font-size: 18px"/>
                    </div>
                </div>
            </div>
            <br /><label></label>
            <md-button class="md-primary md-raised" id="submitbutton" style="font-family: 'Noto Sans KR', sans-serif; font-size: 18px" v-on:click="send_Information"> 발 급 </md-button>
        </div>
        <div id ="result_license">
            <label style="font-size: 20px; padding-bottom: 20px">라이센스 키</label>
            <div style="padding: 20px">

            </div>
            <textarea v-model="this.result" style="height: 550px; width: 95%;font-family: 'Noto Sans KR', sans-serif; font-size: 18px;" name="content" cols="40" rows="8" ></textarea>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import Vue from 'vue';
    import VCalendar from 'v-calendar';

    // Use v-calendar & v-date-picker components
    Vue.use(VCalendar, {});

    import VTooltip from 'v-tooltip'

    Vue.use(VTooltip)


    var data_list = {
        product: '',
        period : '',
        result : '',
        regName : '',
        comP : '',
        hdID : '',
        date: new Date(),
        permanent_check: false
    };

    export default {
        name: 'LicenseSubmit',
        data:function () {
            return data_list
        },
        computed: {
            permanent_check_datepicker:function () {
                if(this.permanent_check){
                    return null;// Date picker의 달력에서 모든 날짜 비활성화 시킨다.
                }
                var _today = new Date();
                var _yesterday = _today.setDate(_today.getDate() -1);
                return _yesterday; // Date picker의 달력에서 오늘이후의 모든 날짜를 활성화 시킨다.
            },
/*            calc_period:function () {
                if(this.permanent_check){
                    return 0;
                }
                return this.day_diff(this.date, new Date());
            },*/
            show_endDate: function () {
                if(this.permanent_check){
                    return null;
                }
                return this.date_format(this.date);
            }
        },
        methods : {
            send_Information : function(){
                if(!(this.regName && this.comP && this.hdID && this.product)){
                    var alert_sentence = "";
                    if(!this.product){
                        alert_sentence += "제품명"
                    }
                    if(!this.regName){
                        if(!alert_sentence){
                            alert_sentence = "발급 요청자"
                        }
                        else{
                            alert_sentence += ", 발급 요청자"
                        }
                    }
                    if(!this.comP){
                        if(!alert_sentence){
                            alert_sentence = "사이트 이름"
                        }
                        else{
                            alert_sentence += ", 사이트 이름"
                        }
                    }
                    if(!this.hdID){
                        if(!alert_sentence){
                            alert_sentence = "하드웨어 아이디"
                        }
                        else{
                            alert_sentence += ", 하드웨어 아이디"
                        }
                    }

                    alert_sentence += " 를 입력해주세요."

                    alert(alert_sentence);
                    return;
                }

                //하드웨어아이디 유효성 검증
                if(!this.hardwareID_validation_check(this.hdID)){
                    alert("하드웨어 아이디를 제대로 적으셨는지 다시 확인해주세요.");
                    return;
                }

                /*if(this.permanent_check === false && this.day_diff(this.date, new Date())<1){
                    alert("오늘날짜를 만료일로 선택할 수 없습니다.");
                    return;
                }*/

                //정보 전송
                axios({
                    method : 'GET',
                    /*url : 'http://localhost:8500/issuelicense',//ToDo 이거 url config같은거로 고정시켜보자*/
                    url : 'http://211.39.140.228:8500/issuelicense',//ToDo 이거 url config같은거로 고정시켜보자
                    params : {
                        product : this.product,
                        regname : this.regName,
                        comp : this.comP,
                        HDid : this.hdID,
                        endDate : this.show_endDate,
                        permanent_check : this.permanent_check
                    }
                }).then((response) => {this.result = response.data})
            },

            date_format : function (var_Date) {//Date()형식의 데이터가 들어와야함
                var _date_format = var_Date;

                var year_format = _date_format.getFullYear().toString();
                var month_format = (_date_format.getMonth() +1).toString().padStart(2,'0');
                var day_format = (_date_format.getDate()).toString().padStart(2,'0');

                var full_date_string = year_format+"-"+month_format+"-"+day_format;
                return full_date_string;
            },
            day_diff : function (post_date, pre_date) {
                var dateDiff = Math.ceil((post_date.getTime()-pre_date.getTime())/(1000*3600*24));
                return dateDiff;
            },


            permanent_check_change : function(){
                if(this.permanent_check){
                    this.date = null;
                    return
                }
                this.date = new Date();

            },
            hardwareID_validation_check : function (hardwareID) {
                //"&&"이 있는지 여부 확인
                if(!hardwareID.includes("&&")){//".includes"는 ES6 이상에서 사용가능
                    return false;
                }

                //글자 갯수가 9 - 4 - 4 - 4- 12 인지 확인하기
                const ids = hardwareID.split("&&");
                let check_serial = [9,4,4,4,12];

                if(!(ids.length===2)){
                    return false;
                }
                for(var i=0; i<ids.length; i++){
                    var id = ids[i].split("-");
                    //총 5개 묶음의 string인지 확인
                    if(!(id.length===5)){
                        return false;
                    }
                    //각각의 글자 갯수 카운트 & 영어소문자, 숫자로만 구성했는지 확인하기
                    for(var j=0; j<id.length; j++){
                        //각각의 글자 갯수 카운트
                        if(!(id[j].length===check_serial[j])){
                            return false;
                        }
                        //영어소문자, 숫자로만 구성했는지 확인하기
                        var regType1 = /^[a-z0-9+]*$/;

                        if(!regType1.test(id[j])){
                            return false;
                        }
                    }
                }

                return true;
            }
        }
    }

</script>

<style scoped>


    #information_input{
        /*border : 1px solid red;*/
        width: 40%;
        float: left;
        margin-left: 30px;
        margin-top: 30px;
        position : relative;
    }

    #result_license{
        /*border : 1px solid red;*/
        width: 50%;
        float: right;
        margin-top: 60px;
        margin-right: 20px;
    }

    #submitbutton{
        position: absolute;
        margin-left: 10%;
        margin-bottom: 10%;
        width: 60px;
        height: 40px;
        font-size: 15px;

    }

    /*#product_period{
        display: grid;
        grid-template-columns: 33% 33% 33%;
    }*/

    #temp{
        display: grid;
        grid-template-columns: 35% 65%;
        grid-template-rows: 60px 85px 85px 85px 85px ;
    }


    .inputsector{
        margin-left : 50px;
        display: grid;
        grid-template-columns: 30% 70%;
        grid-template-rows: 90px 90px 90px ;
        margin-top : 50px;

    }

    .titlefont{

    }

    @font-face {
        font-family: 'Noto Sans KR';
        src: url('../assets/fonts/Noto_Sans_KR/NotoSansKR-Regular.otf');
        font-weight: 500;

    }

    #licens_submit{
        font-family: 'Noto Sans KR', sans-serif;
        font-size: 20px;
    }



</style>
