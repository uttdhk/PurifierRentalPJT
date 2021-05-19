
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import OrderManager from "./components/OrderManager"

import OrderStatus from "./components/orderStatus"
import AssignmentManager from "./components/AssignmentManager"

import InstallationManager from "./components/InstallationManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/Order',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/orderStatus',
                name: 'orderStatus',
                component: orderStatus
            },
            {
                path: '/Assignment',
                name: 'AssignmentManager',
                component: AssignmentManager
            },

            {
                path: '/Installation',
                name: 'InstallationManager',
                component: InstallationManager
            },



    ]
})
