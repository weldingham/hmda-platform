package hmda.validation.rules.lar.quality._2019

import hmda.model.filing.lar.LarGenerators._
import hmda.model.filing.lar.LoanApplicationRegister
import hmda.model.filing.lar.enums._
import hmda.validation.rules.EditCheck
import hmda.validation.rules.lar.LarEditCheckSpec

class Q644Spec extends LarEditCheckSpec {
  override def check: EditCheck[LoanApplicationRegister] = Q644

  property("AUS Result should be valid for the corresponding AUS") {
    forAll(larGen) { lar =>
      lar
        .copy(
          AUS = lar.AUS.copy(aus1 = EmptyAUSValue,
                             aus2 = EmptyAUSValue,
                             aus3 = EmptyAUSValue,
                             aus4 = EmptyAUSValue,
                             aus5 = EmptyAUSValue))
        .mustPass

      val appLar = lar.copy(
        AUS = lar.AUS.copy(aus1 = EmptyAUSValue,
                           aus2 = LoanProspector,
                           aus3 = EmptyAUSValue,
                           aus4 = LoanProspector,
                           aus5 = EmptyAUSValue))

      appLar
        .copy(
          ausResult = appLar.ausResult.copy(
            ausResult1 = EmptyAUSResultValue,
            ausResult2 = EmptyAUSResultValue,
            ausResult3 = EmptyAUSResultValue,
            ausResult4 = EmptyAUSResultValue,
            ausResult5 = EmptyAUSResultValue
          ))
        .mustFail

      appLar
        .copy(
          ausResult = appLar.ausResult.copy(ausResult1 = EmptyAUSResultValue,
                                            ausResult2 = Accept,
                                            ausResult3 = EmptyAUSResultValue,
                                            ausResult4 = Invalid,
                                            ausResult5 = EmptyAUSResultValue))
        .mustPass

      appLar
        .copy(
          ausResult = appLar.ausResult.copy(
            ausResult1 = EmptyAUSResultValue,
            ausResult2 = Accept,
            ausResult3 = EmptyAUSResultValue,
            ausResult4 = OtherAutomatedUnderwritingResult,
            ausResult5 = EmptyAUSResultValue
          ))
        .mustPass
    }
  }
}
