var app = new Vue({
    el: '#app',
    data: {
        page: 1,
        pages: 1,
        name: '',
        text: '',
        todos : [
            {
                name: '',
                text: '',
                timestamp: ''
            }
        ]
    },
    methods: {
        fetchDefault: function () {
            this.fetch(1)
        },
        fetch: _.debounce(function (page) {
          var vm = this
          axios
            .get('/api/todos?page=' + page)
            .then(response => {
              console.log(response.data)
              vm.pages = Math.ceil(response.data.totalCount / response.data.perPage)
              vm.todos = response.data.items
            })
            .catch(error => console.log(error))
        }, 500),
        add: function () {
          var vm = this
          axios
            .post('/api/todos', {
                name: vm.name,
                text: vm.text
            })
            .then(response => {
              vm.page = 1
              this.fetch(vm.page)
            })
            .catch(error => console.log(error))
        }
    },
    watch: {
        'page': function(newVal, oldVal) {
            console.log("called")
            this.fetch(newVal)
        }
    },
    mounted: function() {
        this.fetchDefault()
    }
})