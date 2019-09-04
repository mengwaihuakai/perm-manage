/*一、菜单栏*/
//1、菜单栏选中
document.querySelector("#permRole").classList.add("active");
//2、面包屑
const isEditPage = document.querySelector('#roleInfo');
document.querySelector("#breadcrumbModule").innerHTML = "<a href='/perm/role/permRole'>Role Manage</a>";
document.querySelector("#breadcrumbPage").textContent = isEditPage ? "Edit Role" : "Create Role";

/*二、axios设置*/
const api = axios.create({
    baseURL: '/perm/role',
    timeout: 180000
});
api.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';



/*三、vue*/
let ref;

let app = new Vue({
    el: "#app",
    data: {
        role: {
            name: null,
            code: null,
            status: null,
            permissionIdArr: []
        },
        rules: {
            name: [
                {required: true, message: 'cannot be bull'},
                {min: 1, max: 100, message: 'cannot more than 100 characters'}
            ],
            code: [
                {required: true, message: 'cannot be bull'},
                {min: 1, max: 100, message: 'cannot more than 100 characters'}
            ],
            status: [
                {required: true, message: 'cannot be bull'},
            ],
            permissionIdArr: [
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
        permissionTableData: []
    },
    mounted (){
        ref = this.$refs;
        //组织permlisttable数据
        this.generatePermTableData();
        //赋值
        if (isEditPage) {
            this.editPageData();
        }
    },
    methods: {
        submitForm (formName) {
            ref[formName].validate((valid) => {
                if (valid) {
                    if (isEditPage) {//编辑页面
                        api.post("editRole", Qs.stringify(app.role, { arrayFormat: 'repeat' }))
                            .then((r) => {
                                app.loading = false;
                                if (r.data.code === 0){
                                    app.$alert('编辑role成功', {
                                        center: true,
                                        callback: action => {
                                            window.location.href="/perm/role/permRole";
                                        }
                                    });
                                } else {
                                    app.$alert('编辑role失败', {
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
                        api.post("addRole", Qs.stringify(app.role, { arrayFormat: 'repeat' }))
                            .then((r) => {
                                app.loading = false;
                                if (r.data.code === 0){
                                    app.$alert('创建role成功', {
                                        center: true,
                                        callback: action => {
                                            window.location.href="/perm/role/permRole";
                                        }
                                    });
                                } else {
                                    app.$alert('创建role失败', {
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
        resetForm (formName) {
            ref[formName].resetFields();
            this.permissionTableData = this.permissionTableData.map(item => {//权限表格取消全选
                item.all = [];
                return item;
            })
        },
        generatePermTableData () {
            let permissionList = eval("(" + document.querySelector("#permissionList").textContent + ")");
            let tableData = [];
            for (let i = 0; i < permissionList.length; i++){
                let needAdd = true;
                for (let j = 0; j < tableData.length; j++){
                    if (permissionList[i].fatherCode === tableData[j].fatherCode){
                        needAdd = false;
                        break;
                    }
                }
                if (needAdd){
                    tableData.push({
                        fatherName: permissionList[i].fatherName,
                        fatherCode: permissionList[i].fatherCode,
                        all: [],
                        childPermList: []
                    })
                }
            }
            for (let i = 0; i < permissionList.length; i++){
                for (let j = 0; j < tableData.length; j++){
                    if (permissionList[i].fatherCode === tableData[j].fatherCode){
                        tableData[j].childPermList.push({
                            id: permissionList[i].id,
                            name: permissionList[i].name
                        })
                    }
                }
            }
            this.permissionTableData = tableData;
        },
        editPageData () {
            let vm = this;
            let roleInfo = eval("(" + isEditPage.textContent + ")");
            vm.role.id = roleInfo.id;
            vm.role.name = roleInfo.name;
            vm.role.code = roleInfo.code;
            vm.role.status = roleInfo.status;
            vm.role.permissionIdArr = [];
            roleInfo.permissionList.forEach(item => {
                vm.role.permissionIdArr.push(item.id)
            });
            //permissionTableData是否需要全选
            vm.permissionTableData = vm.permissionTableData.map(fItem => {
                let childPermIdArr = [];
                fItem.childPermList.forEach(cItem => {
                    childPermIdArr.push(cItem.id);
                });
                let selectAll = childPermIdArr.every(pItem => {
                    return vm.role.permissionIdArr.includes(pItem);
                });
                fItem.all = selectAll ? ['all'] : [];
                return fItem;
            })
        },
        selectAll (fatherCode, all) {//全选联动
            let vm = this;
            vm.permissionTableData.forEach(pItem => {
                if (fatherCode === pItem.fatherCode){
                    let permissionIdSet = new Set(vm.role.permissionIdArr);
                    pItem.childPermList.forEach(cItem => {
                        all.length === 0 ? permissionIdSet.delete(cItem.id) : permissionIdSet.add(cItem.id)
                    });
                    vm.role.permissionIdArr = [...permissionIdSet];
                }
            })
        },
        selectOne (fatherCode, id) {//联动全选
            let vm = this;
            vm.permissionTableData = this.permissionTableData.map(item => {
                let selectAll = false;
                if (item.fatherCode === fatherCode){
                    selectAll = item.childPermList.every(item => {
                        return vm.role.permissionIdArr.includes(item.id);
                    });
                    item.all = selectAll ? ['all']: [];
                }
                return item;
            })
        }
    }
});
