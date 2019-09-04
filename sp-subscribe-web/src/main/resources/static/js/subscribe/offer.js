/*一、菜单栏*/
//1、菜单栏选中
document.querySelector("#offer").classList.add("active");
//2、面包屑
const isEditPage = document.querySelector('#editValue');
document.querySelector("#breadcrumbModule").innerHTML = "<a href='/subscribe/offer/listView'>Offer List</a>";
document.querySelector("#breadcrumbPage").textContent = isEditPage ? "Edit Offer" : "Create Offer";

/*二、axios设置*/
const api = axios.create({
    baseURL: '/subscribe/offer',
    timeout: 180000
});
api.defaults.headers.post['Content-Type'] = 'application/json';


/*三、vue*/
let ref;
const checkHttp = (rule, value, callback) => {
    if (value && !(value.startsWith("http://") || value.startsWith("https://"))){
        callback(new Error('please start with "http://" or "https://"'));
    } else {
        callback();
    }
};
const checkNumber = (rule, value, callback) => {
    if (value !== 0 && value && isNaN(Number(value))){
        callback(new Error('please input a number'));
    } else {
        callback();
    }
};
const checkNoNegativeInt = (rule, value, callback) => {
    if (value && (Math.sign(value) < 0 || !Number.isInteger(Number(value)))){
        callback(new Error('please input a positive integer or 0'));
    } else {
        callback();
    }
};
const checkPlusNum = (rule, value, callback) => {
    if (value){
        if (Math.sign(value) <= 0){
            callback(new Error('please input a positive number'));
        } else if (Number(value) > 2147483647){
            callback(new Error('the number input cannot be greater than 2147483647'));
        } else {
            callback();
        }
    } else {
        callback();
    }
};
const checkDescriptionTextarea = (rule, value, callback) => {
    if (value && (value.includes(";"))){
        callback(new Error('cannot input ";"'));
    } else {
        callback();
    }
};
const checkTargetTextarea = (rule, value, callback) => {
    if (value && (value.includes(",") || value.includes("*"))){
        callback(new Error('cannot input "," or "*"'));
    } else {
        callback();
    }
};

let app = new Vue({
   el: "#app",
   data: {
       offer: {
           offer_id: null,
           offer_name: null,
           root_offer_id: null,
           type: null,
           pf_description: null,
           platform: null,
           pf_category: null,
           pf_conversion_flow: null,
           pf_conversion_type: null,
           pf_payout: null,
           pf_kpi: null,
           pf_required_deviceid: 0,
           config_status: 0,
           status: 0,
           effective_date: null,
           url: null,
           is_close_wifi: 0,
           is_notification_enabled: 0,
           budget: null,
           daily_budget: null,
           target_country_mode: 0,
           target_country: [],
           target_operator_mode: 0,
           target_operator: null,
           target_os_mode: 1,
           target_os: 'android|ios'
       },
       rules: {
           root_offer_id: [
               {validator: checkNumber},
               {validator: checkNoNegativeInt}
           ],
           pf_payout: [
               {validator: checkNumber},
               {validator: checkPlusNum}
           ],
           pf_required_deviceid: [
               {required: true, message: 'cannot be bull'}
           ],
           config_status: [
               {required: true, message: 'cannot be bull'}
           ],
           status: [
               {required: true, message: 'cannot be bull'}
           ],
           effective_date: [
               {required: true, message: 'cannot be bull'}
           ],
           url: [
               {required: true, message: 'cannot be bull'},
               {validator: checkHttp}
           ],
           pf_description: [
               {validator: checkDescriptionTextarea}
           ],
           is_close_wifi: [
               {required: true, message: 'cannot be bull'}
           ],
           is_notification_enabled: [
               {required: true, message: 'cannot be bull'}
           ],
           budget: [
               {required: true, message: 'cannot be bull'},
               {validator: checkNumber},
               {validator: checkNoNegativeInt}
           ],
           daily_budget: [
               {validator: checkNumber},
               {validator: checkNoNegativeInt}
           ],
           target_country: [
               {required: false, message: 'cannot be bull'}
           ],
           target_operator: [
               {required: false, message: 'cannot be bull'},
               {validator: checkTargetTextarea}
           ]
       },
       configStatusRadio: [
           {
               label: 'no config',
               value: 0
           },
           {
               label: 'config success',
               value: 1
           }
       ],
       osRadio: [
           {
               value: "android|ios",
               label: 'All'
           },
           {
               value: "android",
               label: 'Android'
           },
           {
               value: 'ios',
               label: 'IOS'
           }
       ],
       targetRadio: [
           {
               value: 0,
               label: 'All'
           },
           {
               value: 1,
               label: 'Include'
           },
           {
               value: 2,
               label: 'Exclude'
           },
       ],
       countryOptions: eval("(" + document.querySelector("#countryOption").textContent + ")"),
       count: {
           country: null
       },
       countShow: {
           country: false
       },
       tagOutSelectStyle: {
           country: 'position: absolute'
       },
       targetValueShow: {
           country: false,
           operator: false
       },
       loading: false
   },
   mounted (){
       ref = this.$refs;
       //赋值
       if (isEditPage) {
           this.offer = JSON.parse(isEditPage.value);
           this.offer.pf_description = this.offer.pf_description.replace(new RegExp(";", "g"), "\n");
           this.offer.target_os = this.offer.target_os == null ? null : this.offer.target_os.join("|").replace(new RegExp("\"", "g"), "");
           this.offer.target_operator = this.offer.target_operator == null ? null : this.offer.target_operator.join("\n").replace(new RegExp("\"", "g"), "");
           this.offer.target_country = this.offer.target_country == null ? null : this.offer.target_country.map(item => {
               return item.replace(new RegExp("\"", "g"), "");
           })
       }
   },
   watch: {
       "offer.target_country": (newVal, oldVal) => {
           if (newVal.length === 0) {
               app.count.country = null;
               app.tagOutSelectStyle.country = "position: absolute";
           } else {
               app.count.country = "Selected: " + newVal.length;
               app.tagOutSelectStyle.country = "position: relative";
           }
       },
       'offer.target_country_mode': (newVal, oldVal) => {
           if (newVal){
               app.rules.target_country[0].required = true;
               app.targetValueShow.country = true;
           } else {
               app.rules.target_country[0].required = false;
               app.offer.target_country = [];
               app.targetValueShow.country = false;
           }
       },
       'offer.target_operator_mode': (newVal, oldVal) => {
           if (newVal){
               app.rules.target_operator[0].required = true;
               app.targetValueShow.operator = true;
           } else {
               app.rules.target_operator[0].required = false;
               app.offer.target_operator = null;
               app.targetValueShow.operator = false;
           }
       }
   },
   methods: {
       submitForm: formName => {
           ref[formName].validate((valid) => {
               if (valid) {
                   if (isEditPage) {//编辑页面
                       api.post("edit", app.genarateData(app.offer))
                           .then((r) => {
                               app.loading = false;
                               if (r.data.code === 0){
                                   app.$alert('编辑offer成功', {
                                       center: true,
                                       callback: action => {
                                           window.location.href="/subscribe/offer/listView";
                                       }
                                   });
                               } else {
                                   app.$alert('编辑offer失败', {
                                       center: true,
                                       callback: action => {}
                                   })
                               }
                           })
                           .catch((e) => {
                               app.loading = false;
                               app.$alert('' + e, '数据保存异常', {
                                   center: true,
                                   callback: action => {}
                               })
                           })
                   } else {//创建页面
                       api.post("new", app.genarateData(app.offer))
                           .then((r) => {
                               app.loading = false;
                               if (r.data.code === 0){
                                   app.$alert('创建offer成功', {
                                       center: true,
                                       callback: action => {
                                           window.location.href="/subscribe/offer/listView";
                                       }
                                   });
                               } else if (r.data.code === 2){
                                   app.$alert('id重复，创建offer失败', {
                                       center: true,
                                       callback: action => {}
                                   })
                               } else {
                                   app.$alert('创建offer失败', {
                                       center: true,
                                       callback: action => {}
                                   })
                               }
                           })
                           .catch((e) => {
                               app.loading = false;
                               app.$alert('' + e, '数据保存异常', {
                                   center: true,
                                   callback: action => {}
                               })
                           })
                   }
               } else {
                   return false;
               }
           });
       },
       resetForm: formName =>{
           ref[formName].resetFields();
       },
       genarateData: (obj) => {
           let data = {...obj};
           data.pf_description = app.generateTextareaData(obj.pf_description).join(";");
           data.target_country = obj.target_country_mode === 0 ? null : obj.target_country;
           data.target_operator = obj.target_operator_mode === 0 ? null : app.generateTextareaData(obj.target_operator);
           data.target_os = obj.target_os.split("|");
           return data;
       },
       generateTextareaData: (value) => {
           let arr = value.split("\n");
           let newArr = [];
           for (let i = 0; i < arr.length; i++){
               if (arr[i].trim()){
                   let add = true;
                   for (let j = 0; j < newArr.length; j++){
                       if (arr[i].trim() === newArr[j]){
                           add = false;
                           break;
                       }
                   }
                   if (add){
                       newArr.push(arr[i].trim())
                   }
               }
           }
           return newArr;
       },
       countryVisiChange: v => {
           app.countShow.country = !v;
       },
       operatorVisiChange: v => {
           app.countShow.operator = !v;
       }
   }
});
