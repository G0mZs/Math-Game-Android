package pt.isec.a21280210_a21280183_a21280330.tp_amov.data

enum class ConnectionState {
    SETTING_PARAMETERS, SERVER_CONNECTING, CLIENT_CONNECTING, CONNECTION_ESTABLISHED,
    CONNECTION_ERROR, CONNECTION_ENDED
}