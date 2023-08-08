const Sequelize = require("sequelize");

class UserState extends Sequelize.Model {
    static initiate(sequelize) {
        UserState.init({
            isConnected: {
                type: Sequelize.BOOLEAN,
                allowNull: false,
                defaultValue: false
            }
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'UserState',
            tableName: 'userStates',
            paranoid: false,
            charset: "utf8mb4",
            collate: "utf8mb4_general_ci"
        });
    }

    static associate(db) { }
}

module.exports = UserState;