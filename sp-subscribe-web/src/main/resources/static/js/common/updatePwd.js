/**
 * Created by ZOOMY on 2018/8/24.
 */

//修改密码：
$('#updatePwdForm').bootstrapValidator({
    live: 'submitted',//验证时机，enabled是内容有变化就验证（默认），disabled和submitted是提交再验证
    excluded: [':disabled', ':hidden', ':not(:visible)'],//排除无需验证的控件，比如被禁用的或者被隐藏的
    submitButtons: '#addUserSubmit',//指定提交按钮，如果验证失败则变成disabled，但我没试成功，反而加了这句话非submit按钮也会提交到action指定页面
    message: 'Failed to verify the form',//通用验证失败消息
    feedbackIcons: {//根据验证结果显示的各种图标
        valid: '',
        invalid: '',
        validating: ''
    },
    fields: {
        password: {
            validators: {
                notEmpty: {
                    message: 'Cannot be null'
                },
                stringLength: {
                    max: 100,
                    message: 'Maximum length is 100'
                },
                identical: {//相同
                    field: 'confirmPwd', //需要进行比较的input name值
                    message: 'Two passwords are inconsistent'
                }
            }
        },
        confirmPwd: {
            validators: {
                notEmpty: {
                    message: 'Cannot be null'
                },
                stringLength: {
                    max: 100,
                    message: 'Maximum length is 100'
                },
                identical: {//相同
                    field: 'password', //需要进行比较的input name值
                    message: 'Two passwords are inconsistent'
                }
            }
        }
    }
});
//提交表单
$("#uPwdBtn").bind("click",function () {//非submit按钮点击后进行验证，如果是submit则无需此句直接验证
    //提交验证
    $("#updatePwdForm").bootstrapValidator('validate');
    //获取验证结果，如果成功，执行下面代码
    if ($("#updatePwdForm").data('bootstrapValidator').isValid()) {
        var params={
            pwd:$("#password").val(),
            confirmPwd:$("#confirmPwd").val()
        }
        $.ajax({
            type:"post",
            url:"updatePwd",
            data:params,
            success:function(data){
                $("#hintModal").modal("show");
                $("#hintBody").text(data.message);
                if (data.code == 0) {//回到publisher daily report页面
                    $("#hintModal").on("hide.bs.modal", function () {
                        window.location.href = "/dsp/publisherDailyReport";
                    });
                }
            },
            error:function(){
                $("#hintModal").modal("show");
                $("#hintBody").text("Failed to modify password, contact administrator");
            }
        });
    }
});








