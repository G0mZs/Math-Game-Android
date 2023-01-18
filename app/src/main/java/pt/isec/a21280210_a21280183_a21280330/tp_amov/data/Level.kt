package pt.isec.a21280210_a21280183_a21280330.tp_amov.data

class Level {

    var number : Int
    var nBoards : Int
    var countdown : Int
    var minAnswers : Int
    var time : Int
    var totalTime : Int

    constructor(number: Int, nBoards : Int, countdown : Int, minAnswers : Int, time : Int, totalTime : Int){
        this.number = number
        this.nBoards = nBoards
        this.countdown = countdown
        this.minAnswers = minAnswers
        this.time = time
        this.totalTime = totalTime
    }

}