package hmda.validation.rules.lar.validity

import hmda.model.filing.lar.LoanApplicationRegister
import hmda.model.filing.lar.enums._
import hmda.validation.dsl.PredicateCommon._
import hmda.validation.dsl.PredicateSyntax._
import hmda.validation.dsl.ValidationResult
import hmda.validation.rules.EditCheck

object V611 extends EditCheck[LoanApplicationRegister] {
  override def name: String = "V611"

  override def apply(lar: LoanApplicationRegister): ValidationResult = {
    lar.loan.loanType is oneOf(Conventional,
                               FHAInsured,
                               VAGuaranteed,
                               RHSOrFSAGuaranteed)
  }
}
