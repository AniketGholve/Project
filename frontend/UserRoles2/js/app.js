
function reloadWindow()
{
    location.reload();
}
let app = angular.module("myApp", ['ngRoute']);

app.config(function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: "view/login.html"
        })
        .when('/register', {
            templateUrl: "view/registration.html"
        })
        .when('/elp_users', {
            templateUrl: "view/elp_users.html"
        })
        .when('/mlp_users', {
            templateUrl: "view/mlp_users.html"
        })
        .when('/clp_users', {
            templateUrl: "view/clp_users.html"
        })
        .when('/alp_users', {
            templateUrl: "view/alp_users.html"
        })
        .when("/updatePatient/:param1", {
            templateUrl: "view/updatePatient.html"
        })
        .when("/insertPatient", {
            templateUrl: "/view/insertPatient.html"
        });
});

app.controller("loginCtrl", ($scope, $http, $window) => {
    $scope.navOption1Link="#!";
    $scope.navOption1="Login";
    $scope.navOption2Link="#!register";
    $scope.navOption2="Register";
    $scope.getRequest = (v) => {
        $http({
            method: 'POST',
            url: 'http://localhost:7890/login',
            data: $scope.submit,
            headers: { 'Content-Type': 'application/json','Authorization':'qweryui' }
        }).then((response) => {
            $scope.data = response.data;
            sessionStorage.setItem("token", "Bearer " + $scope.data.token)
            if ($scope.data) {
                $http({
                    method: 'GET',
                    url: 'http://localhost:7890/api/get/' + $scope.submit.username,
                    headers: { 'Authorization': sessionStorage.getItem("token") }
                }).then((response) => {
                    $scope.data = response.data;
                    if ($scope.data.role == "CLP") {
                        $window.location.href = "#!clp_users";
                    }
                    else if ($scope.data.role == "MLP") {
                        $window.location.href = "#!mlp_users";
                    }
                    else if ($scope.data.role == "ALP") {
                        $window.location.href = "#!alp_users";
                    }
                    else if ($scope.data.role == "ELP") {
                        $window.location.href = "#!elp_users";
                    }
                })
            }
        }, (error) => {
            alert("Wrong User Id Or Password");
        })
    }
});

app.controller("mlp", ($scope, $http) => {
    $scope.hide="d-none";
    $scope.navOption1="Inventory"
    $scope.navOption1Link="#!mlp_users"
    $http({
        method: 'GET',
        url: 'http://localhost:7890/getScreen',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem("token")
        }
    }).then((response) => {
        $scope.data = response.data;
    }, (error) => {
        console.log(error)
    })
    $scope.getOnHand = (id) => {
        $http({
            method: 'GET',
            url: "http://localhost:7890/getSerialNumber/" + id,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': sessionStorage.getItem("token")
            }
        }).then((response) => {
            $scope.onHandData = response.data;
        }, (error) => {
            console.log(error)
        })
    }
    $scope.getExpired = (id) => {
        $http({
            method: 'GET',
            url: "http://localhost:7890/getExpiredSerialDetails/" + id,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': sessionStorage.getItem("token")
            }
        }).then((response) => {
            $scope.expiredData = response.data;
        }, (error) => {
            console.log(error)
        })
    }
});

app.controller("clp", function ($scope, $http) {
    $scope.navOption1Link="#!/clp_users";
    $scope.navOption1="Patient";
    $scope.navOption2Link="#!/insertPatient";
    $scope.navOption2="Insert Patient";
    $http({
        method: 'GET',
        url: "http://localhost:7890/getAllData",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem("token")
        }
    }).then((response) => {
        $scope.allPatientData = response.data;
    }, (error) => {
        console.log(error)
    })

    $scope.getById = () => {
        $http({
            method: 'GET',
            url: "http://localhost:7890/getPatientById/" + $scope.variable,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': sessionStorage.getItem("token")
            }
        }).then((response) => {
            $scope.searchPatientData = response.data;
        }, (error) => {
            $scope.searchPatientData = null;
            alert("No Data Found");
        })
    }
    $scope.deleteById = (id) => {
        $http({
            method: 'delete',
            url: "http://localhost:7890/deletePatient/" + id,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': sessionStorage.getItem("token")
            }
        }).then((response) => { }, (error) => { })
    }
});

app.controller('registerController', function ($scope, $http,$window) {
    $scope.navOption1Link="#!";
    $scope.navOption1="Login";
    $scope.navOption2Link="#!register";
    $scope.navOption2="Register";

    $scope.register={};
    $scope.formData = () => {
        console.log($scope.register);
        $http({
            method: 'Post',
            url: "http://localhost:7890/api/addUser",
            headers: {'Content-Type': 'application/json'},
            data:$scope.register
        }).then((response)=>{
            $window.location.href = "#!";
        },(error)=> {
            console.log(error);
        });
    };
});


app.controller('updateController', function ($scope, $http, $routeParams ,$window) {
    $scope.navOption1Link="#!/clp_users";
    $scope.navOption1="Patient";
    $scope.hide="d-none";
    $http({
        method: 'GET',
        url: "http://localhost:7890/getPatientById/" + $routeParams.param1,
        data: $scope.updateFormData,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem("token")
        }
    }).then((response) => {
        $scope.updateFormData = response.data;
        $scope.updateFormData.patientDob = new Date(response.data.patientDob);
    }, (error) => {
        console.log(error);
    })
    $scope.updatePatientForm = function () {
        $http({
            method: 'PUT',
            url: 'http://localhost:7890/updatePatient',
            data: $scope.updateFormData,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': sessionStorage.getItem("token")
            }
        }).then(function (data) {
            if (data.errors) {
                $scope.errorUserName = data.errors;
                alert("Error Occured No Data was changed");
            } else {
                alert("Data Updated Successfully");
                $window.location.href = "#!/clp_users";
            }
        });
    };
});

app.controller('insertController', function ($scope, $http,$window) {
    $scope.navOption1Link="#!/clp_users";
    $scope.navOption1="Patient";
    $scope.hide="d-none";
    $scope.submit = {};
    $scope.fileData;
    $scope.submitForm = function () {
        $http({
            method: 'POST',
            url: 'http://localhost:7890/createPatient',
            data: $scope.formDataFields,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': sessionStorage.getItem("token")
            }
        }).then(function (data) {
            if (data.errors) {
                $scope.errorUserName = data.errors;
            } else {
                alert("Data Added Successfully");
                $window.location.href = "#!clp_users";
                $scope.formDataFields = null;
            }
        });
    };
});