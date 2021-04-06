let appData = {
    // domain: 'https://boc.pay.zhongyunkj.cn',
    domain: '',
    phone: '',
    integral: '',
    token: '',
    vs: '1.0.0',
    products: '',
    share: '',
    ok: 200
}

function isNumber(val) {
    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if(regPos.test(val) || regNeg.test(val)) {
        return true;
    } else {
        return false;
    }
}

function tips(msg){
    $('#msg').html(msg)
    $('.tishi_bg').show()
}

function isEmpty(data) {
    if (data === null || data === '' || data === undefined || data === 'undefined' || data === 'null') {
        return true;
    } else {
        return false;
    }
}

function getAddressBarParam(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    let r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

var _local = {
    //存储,可设置过期时间
    set(key, value, expires) {
        let params = {key, value, expires};
        if (expires) {
            // 记录何时将值存入缓存，毫秒级
            var data = Object.assign(params, {startTime: new Date().getTime()});
            localStorage.setItem(key, JSON.stringify(data));
        } else {
            if (Object.prototype.toString.call(value) == '[object Object]') {
                value = JSON.stringify(value);
            }
            if (Object.prototype.toString.call(value) == '[object Array]') {
                value = JSON.stringify(value);
            }
            localStorage.setItem(key, value);
        }
    },
    //取出
    get(key) {
        let item = localStorage.getItem(key);
        // 先将拿到的试着进行json转为对象的形式
        try {
            item = JSON.parse(item);
        } catch (error) {
            // eslint-disable-next-line no-self-assign
            item = item;
        }
        // 如果有startTime的值，说明设置了失效时间
        if (item && item.startTime) {
            let date = new Date().getTime();
            // 如果大于就是过期了，如果小于或等于就还没过期
            if (date - item.startTime > item.expires) {
                localStorage.removeItem(name);
                return false;
            } else {
                return item.value;
            }
        } else {
            return item;
        }
    },
    // 删除
    remove(key) {
        localStorage.removeItem(key);
    },
    // 清除全部
    clear() {
        localStorage.clear();
    }
}
