package hmda.publication.reports.aggregate

import akka.NotUsed
import akka.stream.scaladsl.Source
import hmda.model.fi.lar.LoanApplicationRegister
import hmda.model.publication.reports.Disposition
import hmda.model.publication.reports.EthnicityEnum._
import hmda.model.publication.reports.GenderEnum.{ Female, JointGender, Male }
//import hmda.model.publication.reports.ApplicantIncomeEnum._
//import hmda.model.publication.reports.{ ApplicantIncome, Disposition, MSAReport }
//import hmda.model.publication.reports.MinorityStatusEnum._
import hmda.model.publication.reports.RaceEnum._
import hmda.publication.reports._
import hmda.publication.reports.util.GenderUtil.filterGender
import hmda.publication.reports.util.RaceUtil.filterRace
import hmda.publication.reports.util.EthnicityUtil.filterEthnicity
//import hmda.publication.reports.util.MinorityStatusUtil.filterMinorityStatus
import hmda.publication.reports.util.DispositionType._
import hmda.publication.reports.util.ReportUtil._

import scala.concurrent.Future
import spray.json._

object A42 {
  val dispositions = List(ApplicationReceived, LoansOriginated)
  val genders = List(Male, Female, JointGender)

  def generate[ec: EC, mat: MAT, as: AS](
    larSource: Source[LoanApplicationRegister, NotUsed],
    fipsCode: Int
  ): Future[JsValue] = {

    val incomeIntervals = larsByIncomeInterval(larSource.filter(lar => lar.applicant.income != "NA"), calculateMedianIncomeIntervals(fipsCode))
    val msa = msaReport(fipsCode.toString).toJsonFormat
    val reportDate = formattedCurrentDate
    val yearF = calculateYear(larSource)

    for {
      year <- yearF
      e1 <- dispositionsOutput(filterEthnicity(larSource, HispanicOrLatino))
      e1g <- dispositionsByGender(filterEthnicity(larSource, HispanicOrLatino))
      e2 <- dispositionsOutput(filterEthnicity(larSource, NotHispanicOrLatino))
      e2g <- dispositionsByGender(filterEthnicity(larSource, NotHispanicOrLatino))
      e3 <- dispositionsOutput(filterEthnicity(larSource, JointEthnicity))
      e3g <- dispositionsByGender(filterEthnicity(larSource, JointEthnicity))
      e4 <- dispositionsOutput(filterEthnicity(larSource, NotAvailable))
      e4g <- dispositionsByGender(filterEthnicity(larSource, NotAvailable))

      r1 <- dispositionsOutput(filterRace(larSource, AmericanIndianOrAlaskaNative))
      r1g <- dispositionsByGender(filterRace(larSource, AmericanIndianOrAlaskaNative))
      r2 <- dispositionsOutput(filterRace(larSource, Asian))
      r2g <- dispositionsByGender(filterRace(larSource, Asian))
      r3 <- dispositionsOutput(filterRace(larSource, BlackOrAfricanAmerican))
      r3g <- dispositionsByGender(filterRace(larSource, BlackOrAfricanAmerican))
      r4 <- dispositionsOutput(filterRace(larSource, HawaiianOrPacific))
      r4g <- dispositionsByGender(filterRace(larSource, HawaiianOrPacific))
      r5 <- dispositionsOutput(filterRace(larSource, White))
      r5g <- dispositionsByGender(filterRace(larSource, White))
      r6 <- dispositionsOutput(filterRace(larSource, NotProvided))
      r6g <- dispositionsByGender(filterRace(larSource, NotProvided))
      r7 <- dispositionsOutput(filterRace(larSource, TwoOrMoreMinority))
      r7g <- dispositionsByGender(filterRace(larSource, TwoOrMoreMinority))
      r8 <- dispositionsOutput(filterRace(larSource, JointRace))
      r8g <- dispositionsByGender(filterRace(larSource, JointRace))

      total <- dispositionsOutput(larSource)
    } yield {
      s"""
         |{
         |    "table": "4-2",
         |    "type": "Aggregate",
         |    "description": "Disposition of applications for conventional home-purchase loans 1- to 4- family and manufactured home dwellings, by race, ethnicity, gender and income of applicant",
         |    "year": "$year",
         |    "reportDate": "$reportDate",
         |    "msa": $msa,
         |    "ethnicities": [
         |        {
         |            "ethnicity": "Hispanic or Latino",
         |            "dispositions": $e1,
         |            "genders": $e1g
         |        },
         |        {
         |            "ethnicity": "Not Hispanic or Latino",
         |            "dispositions": $e2,
         |            "genders": $e2g
         |        },
         |        {
         |            "ethnicity": "Joint (Hispanic or Latino/Not Hispanic or Latino)",
         |            "dispositions": $e3,
         |            "genders": $e3g
         |        },
         |        {
         |            "ethnicity": "Ethnicity Not Available",
         |            "dispositions": $e4,
         |            "genders": $e4g
         |        }
         |    ],
         |    "races": [
         |        {
         |            "race": "American Indian/Alaska Native",
         |            "dispositions": $r1,
         |            "genders": $r1g
         |        },
         |        {
         |            "race": "Asian",
         |            "dispositions": $r2,
         |            "genders": $r2g
         |        },
         |        {
         |            "race": "Black or African American",
         |            "dispositions": $r3,
         |            "genders": $r3g
         |        },
         |        {
         |            "race": "Native Hawaiian or Other Pacific Islander",
         |            "dispositions": $r4,
         |            "genders": $r4g
         |        },
         |        {
         |            "race": "White",
         |            "dispositions": $r5,
         |            "genders": $r5g
         |        },
         |        {
         |            "race": "2 or more minority races",
         |            "dispositions": $r6,
         |            "genders": $r6g
         |        },
         |        {
         |            "race": "Joint (White/Minority Race)",
         |            "dispositions": $r7,
         |            "genders": $r7g
         |        },
         |        {
         |            "race": "Race Not Available",
         |            "dispositions": $r8,
         |            "genders": $r8g
         |        }
         |    ],
         |    "total": $total
         |}
         |
       """.stripMargin.parseJson
    }
  }

  private def dispositionsOutput[ec: EC, mat: MAT, as: AS](larSource: Source[LoanApplicationRegister, NotUsed]): Future[String] = {
    val calculatedDispositions: Future[List[Disposition]] = Future.sequence(
      dispositions.map(_.calculateDisposition(larSource))
    )

    calculatedDispositions.map { list =>
      list.map(disp => disp.toJsonFormat).mkString("[", ",", "]")
    }
  }

  private def dispositionsByGender[ec: EC, mat: MAT, as: AS](larSource: Source[LoanApplicationRegister, NotUsed]): Future[String] = {
    for {
      male <- dispositionsOutput(filterGender(larSource, Male))
      female <- dispositionsOutput(filterGender(larSource, Female))
      joint <- dispositionsOutput(filterGender(larSource, JointGender))
    } yield {

      s"""
       |
       |[
       | {
       |     "gender": "Male",
       |     "dispositions": $male
       | },
       | {
       |     "gender": "Female",
       |     "dispositions": $female
       | },
       | {
       |     "gender": "Joint (Male/Female)",
       |     "dispositions": $joint
       | }
       |]
       |
     """.stripMargin

    }
  }

}

/*

         |    "minorityStatuses": [
         |        {
         |            "minorityStatus": "White Non-Hispanic",
         |            "dispositions": $m1,
         |            "genders": $m1g
         |        },
         |        {
         |            "minorityStatus": "Others, Including Hispanic",
         |            "dispositions": $m2,
         |            "genders": $m2g
         |        }
         |    ],
         |    "incomes": [
         |        {
         |            "income": "Less than 50% of MSA/MD median",
         |            "dispositions": $i1
         |        },
         |        {
         |            "income": "50-79% of MSA/MD median",
         |            "dispositions": $i2
         |        },
         |        {
         |            "income": "80-99% of MSA/MD median",
         |            "dispositions": $i3
         |        },
         |        {
         |            "income": "100-119% of MSA/MD median",
         |            "dispositions": $i4
         |        },
         |        {
         |            "income": "120% or more of MSA/MD median",
         |            "dispositions": $i5
         |        },
         |        {
         |            "income": "Income Not Available",
         |            "dispositions": $i6
         |        }
         |    ],
 */
