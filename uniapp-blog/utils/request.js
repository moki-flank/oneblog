export default function request(options) {
    //后端接口地址
   //const baseUrl = 'https://www.shiyit.com/mojian';
   const baseUrl = 'http://localhost:8800';
   const token = uni.getStorageSync("token")
   const {
       method,
       url,
       data,
       header
   } = options
   return new Promise((resolve, reject) => {
       uni.request({
           method: method,
           url: baseUrl + url, 
           data,
           header:{"Authorization":token},
       }).then((response) => {
           let [error, res] = response;
           resolve(res.data);
       }).catch(error => {
           let [err, res] = error;
           reject(err)
       })
   })
}