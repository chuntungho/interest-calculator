axios.interceptors.request.use(function(config) {
	vm.loading = true;
	return config;
}, function(error) {
	return Promise.reject(error);
});

axios.interceptors.response.use(function(response) {
	vm.loading = false;
	return response;
}, function(error) {
	return Promise.reject(error);
});


var vm = new Vue({
	el : '#app',
	data : {
		loading : false,
		resp : null,
		req : {method: '', capital: 10000, apr: 12, duration: 12    }
	},


	methods : {
		calculate : function() {
			var that = this;

			var params = new URLSearchParams();
			params.append('method', that.req.method);
			params.append('capital', that.req.capital);
			params.append('apr', that.req.apr);
			params.append('duration', that.req.duration);

			axios.post('calculate', params).then(function(httpResp) {
				console.log(httpResp);

				that.resp = httpResp.data.data;
			}).catch(function(error){
			    var msg = "System busy, please visit later";
				if(error.response){
				   var resp = error.response.data;
				   if (resp.errors) {
				        // just show first error message
				        msg = resp.errors[0].defaultMessage
				   } else {
				        msg = resp.message;
				   }
				}
				alert(msg);
			});
		}
	}
});
