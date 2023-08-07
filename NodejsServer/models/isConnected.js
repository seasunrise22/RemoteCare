const Sequelize = require("sequelize");

class IsConnected extends Sequelize.Model {
    static initiate(sequelize) {
        IsConnected.init({
            isConnected: {
                type: Sequelize.BOOLEAN,
                allowNull: false,
                defaultValue: false
            }
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'IsConnected',
            tableName: 'isconnecteds',
            paranoid: false,
            charset: "utf8mb4",
            collate: "utf8mb4_general_ci"
        });
    }

    static associate(db) {
        db.IsConnected.belongsTo(db.User, { foreignKey: 'userId', onDelete: 'CASCADE' });
    }
}

module.exports = IsConnected;