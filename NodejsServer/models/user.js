const Sequelize = require('sequelize');

class User extends Sequelize.Model {
    static initiate(sequelize) {
        User.init({
            userId: {
                type: Sequelize.STRING(20), // 최대 20자
                allowNull: false,
                unique: true,
            },
            userPassword: {
                type: Sequelize.STRING(100),
                allowNull: false,
            },
            userPosition: {
                type: Sequelize.ENUM('caller', 'receiver'),
                allowNull: false,
            }
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'User',
            tableName: 'users',
            paranoid: true,
            charset: 'utf8',
            collate: 'utf8_general_ci',
        });        
    }
}

module.exports = User;