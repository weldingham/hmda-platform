package hmda.model.fi

import hmda.model.fi.lar.LoanApplicationRegister
import hmda.model.fi.ts.TransmittalSheet

case class FIData(ts: TransmittalSheet, lars: Seq[LoanApplicationRegister])
