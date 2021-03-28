<template>
    <div>
        <md-button class="md-primary md-raised" style="float: right; width: 120px; height: 40px;margin-right: 1%; font-size: 15px;" v-on:click="getExcel"> 엑셀 다운로드 </md-button>
        <vue-table-dynamic :params="params" ref="table"></vue-table-dynamic>
    </div>
</template>

<script>
    import VueTableDynamic from 'vue-table-dynamic'
    import axios from 'axios'
    export default {
        name: "LicenseIssuanceRecord",
        components:{VueTableDynamic},
        data:function () {
            return{
                params:{
                    data:[
                        /*['타임스탬프','신청일', '제품명', '만료일', '발급 신청인', '사이트 이름', '하드웨어 아이디']*/
                        /*['발급 신청일', '제품명', '만료일', '발급 신청인', '사이트 이름', '하드웨어 아이디']*/
                        ['발급 신청일', '제품명', '만료일', '발급 신청인', '사이트 이름']
                    ],
                    header: 'row',
                    enableSearch: true,
                    border: true,
                    stripe: true,
                    pagination: true,
                    pageSize: 5,
                    pageSizes: [5,10,15,20],
                    columnWidth: [{column:0,width:'20%'},{column:1,width:'20%'},{column:2,width:'20%'},{column:3,width:'20%'},{column:4,width:'20%'}],
                    rowHeight: 50,
                    headerHeight : 50
                }
            }
        },
        created(){
            this.get_data();
        },
        methods:{
            refresh: function () {
                //this.params.data.push([1, 'b3ba90', '7c95f7', '9a3853']);
                this.pop();
                this.$refs.table.getData();
            },
            pop: function(){
                var end_num = this.params.data.length;
                for(var num=1; num< end_num; num++) {
                    this.params.data.pop();
                }
            },
            get_data: function () {
                //get로 정보 받아오기
                axios({
                    method : 'GET',
                    /*url : 'http://localhost:8500/getRecord'*/
                    url : 'http://211.39.140.228:8500/getRecord'
                }).then((response) => {
                    for(var idx=0;idx<response.data.length;idx++){
                        var list = [];
                        //list.push(response.data[idx]['_id']['timestamp']);
                        list.push(response.data[idx]['issuedTime']);
                        list.push(response.data[idx]['product']);
                        list.push(response.data[idx]['endDate']);
                        list.push(response.data[idx]['issuedPerson']);
                        list.push(response.data[idx]['siteName']);
                        //list.push(response.data[idx]['hardwareID']);
                        //list.push(response.data[idx]['licensekey']);

                        this.params.data.push(list);
                    }
                });
            },
            reload:function () {
                this.pop();
                this.get_data();
                this.$refs.table.getData();
            },
            getExcel:function () {
                axios({
                    method: 'GET',
                    /*url: 'http://localhost:8500/getExcel',*/
                    url: 'http://211.39.140.228:8500/getExcel',
                    responseType: 'blob'
                }).then(response => {
                    const url = window.URL.createObjectURL(new Blob([response.data], { type: response.headers['content-type'] }));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute('download', '라이센스발급기록.xlsx');//파일이름 정하기
                    document.body.appendChild(link);
                    link.click();
                })
            }
        }
    }
</script>

<style scoped>


</style>