//package pl.fsm
//
//import com.couchbase.client.java.error.CASMismatchException
//import com.couchbase.client.java.error.DocumentAlreadyExistsException
//import com.gpplatform.chat.domain.Account
//import com.gpplatform.chat.domain.ChannelId
//import com.gpplatform.chat.domain.ChatHistory
//import com.gpplatform.chat.domain.Message.ChatType
//import com.gpplatform.chat.domain.Participant
//import com.gpplatform.chat.domain.Universe
//import com.gpplatform.chat.repositories.ChatRepository.ChatHistoryWithCAS
//
//import scalaz.{-\/, \/-}
//
//class FSMTest {
//  sealed trait State
//  case object CheckingForChat extends State
//  case object AddingParticipant extends State
//  case object CreatingChat extends State
//  case object Done extends State
//
//  sealed trait Data
//  case class ChatData(universe: Universe, chatType: ChatType, channelId: ChannelId, account: Account) extends Data
//  case class ExistingChat(chatData: ChatData, chat: ChatHistory, cas: Long) extends Data
//  case class Result(chat: ChatHistory) extends Data
//
//  val f = new FSMMy[State, Data] {
//    startWith(CheckingForChat, ChatData(universe, chatType, channelId, account))
//
//    when(CheckingForChat) {
//      case Event(\/-(ChatHistoryWithCAS(chat, cas)), data: ChatData) => goto(AddingParticipant) using (ExistingChat(data, chat, cas))
//      case Event(-\/(e: NoSuchElementException), data: ChatData) => goto(CreatingChat) using (data)
//    }
//
//    when(AddingParticipant) {
//      case Event(\/-(ChatHistoryWithCAS(chat, _)), _) => goto(Done) using (Result(chat))
//      case Event(-\/(e: CASMismatchException), data: ExistingChat) => goto(CheckingForChat) using (data)
//    }
//
//    when(CreatingChat) {
//      case Event(\/-(ChatHistoryWithCAS(chat, _)), _) => goto(Done) using (Result(chat))
//      case Event(-\/(e: DocumentAlreadyExistsException), data: ChatData) => goto(CheckingForChat) using (data)
//    }
//
//    when(Done) {
//      case _ => stay()
//    }
//
//    onTransition {
//      case CheckingForChat -> AddingParticipant =>
//        nextStateData match {
//          case ExistingChat(ChatData(universe, chatType, channelId, account), chat, cas) =>
//            val newParticipant = Participant(account, Participant.Roles.Assignee)
//            updateChat(universe, chatType, channelId, chat.add(newParticipant), cas).onSuccess {
//              case \/-(d: ChatHistoryWithCAS) => self ! d
//              case -\/(e: CASMismatchException) => self ! e
//            }
//        }
//
//      case CheckingForChat -> CreatingChat =>
//        nextStateData match {
//          case ChatData(universe, chatType, channelId, account) =>
//            val owner = Participant(account, Participant.Roles.Owner)
//            createChat(universe, chatType, channelId, ChatHistory.empty.add(owner)).onSuccess {
//              case \/-(d: ChatHistoryWithCAS) => self ! d
//              case -\/(e: DocumentAlreadyExistsException) => self ! e
//            }
//        }
//
//      case _ -> CheckingForChat =>
//        nextStateData match {
//          case ChatData(universe, chatType, channelId, account) =>
//            get(universe, chatType, channelId).onSuccess {
//              case \/-(d: ChatHistoryWithCAS) => self ! d
//              case -\/(e: NoSuchElementException) => self ! e
//            }
//        }
//      case _ -> Done =>
//    }
//  }
//
//  f.result
//}
