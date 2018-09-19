package hmda.serialization.submission

import java.io.NotSerializableException

import akka.actor.ExtendedActorSystem
import akka.actor.typed.ActorRefResolver
import akka.serialization.SerializerWithStringManifest
import akka.actor.typed.scaladsl.adapter._
import hmda.messages.submission.SubmissionCommands.{
  CreateSubmission,
  GetSubmission,
  ModifySubmission
}
import hmda.model.filing.submission.Submission
import SubmissionProtobufConverter._
import SubmissionCommandsProtobufConverter._
import hmda.persistence.serialization.submission.SubmissionMessage

class SubmissionCommandsSerializer(system: ExtendedActorSystem)
    extends SerializerWithStringManifest {

  private val resolver = ActorRefResolver(system.toTyped)

  override def identifier: Int = 103

  final val SubmissionManifest = classOf[Submission].getName
  final val CreateSubmissionManifest = classOf[CreateSubmission].getName
  final val ModifySubmissionManifest = classOf[ModifySubmission].getName
  final val GetSubmissionManifest = classOf[GetSubmission].getName

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case s: Submission =>
      submissionToProtobuf(s).toByteArray
    case cmd: CreateSubmission =>
      createSubmissionToProtobuf(cmd, resolver).toByteArray
    case cmd: ModifySubmission =>
      modifySubmissionToProtobuf(cmd, resolver).toByteArray
    case cmd: GetSubmission =>
      getSubmissionToProtobuf(cmd, resolver).toByteArray
    case _ =>
      throw new IllegalArgumentException(
        s"Cannot serialize object of type [${o.getClass.getName}]")
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case SubmissionManifest =>
        submissionFromProtobuf(SubmissionMessage.parseFrom(bytes))
      case CreateSubmissionManifest =>
        createSubmissionFromProtobuf(bytes, resolver)
      case ModifySubmissionManifest =>
        modifySubmisstionFromProtobuf(bytes, resolver)
      case GetSubmissionManifest =>
        getSubmissionFromProtobuf(bytes, resolver)
      case _ =>
        throw new NotSerializableException(
          s"Unimplemented deserialization of message with manifest [$manifest] in [${getClass.getName}]")
    }

}
