pragma solidity ^0.5.0;
pragma experimental ABIEncoderV2;

import "./WeCrossHub.sol";

contract AssurerInterchain {
    WeCrossHub hub;

    function init(address _hub) public {
        hub = WeCrossHub(_hub);
    }

    function depositeInterchain(string memory _path, string memory _method, string memory id, string memory amount, string memory _callbackPath, string memory _callbackMethod) public
    returns(string memory)
    {
        string[] memory args = new string[](1);
        args[0] = id;
        args[1] = amount;

        return hub.interchainInvoke(_path, _method, args, _callbackPath, _callbackMethod);
    }

    function depositeCallback(bool state, string[] memory _result) public
    returns(string[] memory)
    {
        if(state) {
            cars[stringToUint(_result[0])].balance -= stringToUint(_result[1]);
        }

        return _result;
    }


    struct data {
        string timestamp;
        string velocity;
        string acceleration;
        string angle;
        string over_speed;
        string rapid_acc;
        string rapid_turn;
    }

    struct car {
        uint balance;
        uint premium;
        mapping(string => data) all_data;
    }

    uint registerCount = 0;

    mapping(uint => car) cars;

    constructor () public {
        //_owner = msg.sender;
    }

    function register(uint id) public {
        // if car does not exist
        cars[id].balance = 0;
        cars[id].premium = 0;
        registerCount += 1;
    }
    function getRegisterCount() public view returns(uint) {
        return registerCount;
    }
    function setRegisterCount(uint _count) public {
        registerCount = _count;
    }

    function deposite(string memory id, string memory amount) public returns(string memory, string memory) {
        cars[stringToUint(id)].balance += stringToUint(amount);
        return (id, amount);
    }
    function getBalance(uint id) public view returns (uint) {
        return cars[id].balance;
    }

    function addData(uint id, string memory timestamp, string memory velocity,
        string memory acceleration, string memory angle, string memory over_speed,
        string memory rapid_acc, string memory rapid_turn) public {

        cars[id].all_data[timestamp] = data(timestamp, velocity, acceleration,
            angle, over_speed, rapid_acc, rapid_turn);
    }

    function getData(uint id, string memory timestamp) public view returns (string memory,
    string memory, string memory, string memory, string memory, string memory,
    string memory) {
        data memory d = cars[id].all_data[timestamp];
        return (d.timestamp, d.velocity, d.acceleration, d.angle, d.over_speed,
            d.rapid_acc, d.rapid_turn);
    }

    function setPremium(uint id, uint amount) public {
        cars[id].premium = amount;
    }

    function getPremium(uint id) public view returns (uint) {
        return cars[id].premium;
    }

    function deduct(uint id) public {
        if(cars[id].balance >= cars[id].premium) {
            cars[id].balance = cars[id].balance - cars[id].premium;
            cars[id].premium = 0;
        }
    }

    function stringToUint(string memory s) public pure returns(uint) {
        bytes memory b = bytes(s);
        uint result = 0;
        for(uint i = 0; i < b.length; i++) {
            if(uint8(b[i]) >= 48 && uint8(b[i]) <= 57) {
                result = result * 10 + (uint8(b[i]) - 48);
            }
        }
        return result;
    }
}
