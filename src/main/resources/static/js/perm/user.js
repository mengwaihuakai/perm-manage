/*一、菜单栏*/
//1、菜单栏选中
document.querySelector("#permUser").classList.add("active");
//2、面包屑
const isEditPage = document.querySelector('#userInfo');
document.querySelector("#breadcrumbModule").innerHTML = "<a href='/perm/user/permUser'>User Manage</a>";
document.querySelector("#breadcrumbPage").textContent = isEditPage ? "Edit User" : "Create User";

/*二、axios设置*/
const api = axios.create({
    baseURL: '/perm/user',
    timeout: 180000
});
api.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';



/*三、vue*/
let ref;

const checkConfirmPwd = (rule, value, callback) => {
    if (value && value !== app.user.password) {
        callback(new Error('the two password is inconsistent'));
    } else {
        callback();
    }
};

let app = new Vue({
    el: "#app",
    data: {
        user: {
            account: null,
            password: null,
            confirmPwd: null,
            status: null,
            roleIdArr: []
        },
        rules: {
            account: [
                {required: true, message: 'cannot be bull'},
                {min: 1, max: 50, message: 'cannot more than 50 characters'}
            ],
            password: [
                {required: true, message: 'cannot be bull'},
                {min: 1, max: 100, message: 'cannot more than 100 characters'}
            ],
            confirmPwd: [
                {required: true, message: 'cannot be bull'},
                {min: 1, max: 100, message: 'cannot more than 100 characters'},
                {validator: checkConfirmPwd}
            ],
            status: [
                {required: true, message: 'cannot be bull'},
            ],
            roleIdArr: [
                {required: true, message: 'cannot be bull', type: 'array'},
            ]
        },
        statusRadio: [
            {
                label: 'Valid',
                value: 0
            },
            {
                label: 'Invalid',
                value: 1
            },
            {
                label: 'Frozen',
                value: 2
            }
        ],
        roleCheckbox: eval("(" + document.querySelector("#roleList").textContent + ")")
    },
    mounted (){
        this.checkRoleExist();
        ref = this.$refs;
        //赋值
        if (isEditPage) {
            this.editPageData();
        }
    },
    methods: {
        submitForm: formName => {
            ref[formName].validate((valid) => {
                if (valid) {
                    if (isEditPage) {//编辑页面
                        api.post("editUser", Qs.stringify(app.user, { arrayFormat: 'repeat' }))
                            .then((r) => {
                                app.loading = false;
                                if (r.data.code === 0){
                                    app.$alert('编辑user成功', {
                                        center: true,
                                        callback: action => {
                                            window.location.href="/perm/user/permUser";
                                        }
                                    });
                                } else {
                                    app.$alert('编辑user失败', {
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
                        api.post("addUser", Qs.stringify(app.user, { arrayFormat: 'repeat' }))
                            .then((r) => {
                                app.loading = false;
                                if (r.data.code === 0){
                                    app.$alert('创建user成功', {
                                        center: true,
                                        callback: action => {
                                            window.location.href="/perm/user/permUser";
                                        }
                                    });
                                } else {
                                    app.$alert('创建user失败', {
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
        editPageData () {
            let userInfo = eval("(" + isEditPage.textContent + ")");
            this.user.id = userInfo.id;
            this.user.account = userInfo.account;
            this.user.password = userInfo.password;
            this.user.status = userInfo.status;
            this.user.confirmPwd = userInfo.password;
            this.user.roleIdArr = [];
            userInfo.roleList.forEach(item => {
                this.user.roleIdArr.push(item.id)
            })
        },
        checkRoleExist(){
            if (!this.roleCheckbox || this.roleCheckbox.length === 0){
                this.$alert('请先在角色管理模块 创建角色', {
                    center: true,
                    callback: action => {}
                })
            }
        }
    }
});
