# Evalia extracted docs and code summary

## Top docs (README, DESIGN-GUIDE, HELP)
=== DOC FILES ===

## Controller & mapping summary (first 200 lines)

## Java code summary (excerpts of Javadocs, classes, fields)
==== java/com/example/evaliaproject/EvaliaProjectApplication.java ====
Class: EvaliaProjectApplication (class)


==== java/com/example/evaliaproject/ai/MistralConfig.java ====
Class: MistralConfig (class)
Fields:
  private String baseUrl
  private String apiKey


==== java/com/example/evaliaproject/ai/ChatController.java ====
Class: ChatController (class)
Mappings:
  @RequestMapping paths=['/api/ai']
  @PostMapping paths=['/chat']
Fields:
  private final WebClient mistral
  private String model


==== java/com/example/evaliaproject/service/QuizService.java ====
Class: QuizService (class)


==== java/com/example/evaliaproject/service/ICategoryService.java ====
Class: ICategoryService (interface)


==== java/com/example/evaliaproject/service/INotificationService.java ====
Class: INotificationService (interface)


==== java/com/example/evaliaproject/service/ResponsePanelisteService.java ====
Class: ResponsePanelisteService (class)
Fields:
  private ResponsePanelisteRepository responsePanelisteRepository


==== java/com/example/evaliaproject/service/NotificationService.java ====
Class: NotificationService (class)
Javadoc:
/**
 * Service applicatif pour créer/lire/mettre à jour les notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository repo;
    private final ApplicationEventPublisher events;
    private final NotificationStreamService streamService;

    /** Event interne pour déclencher le push SSE après commit. */
    public record NotificationCreatedEvent(Notification notif) {}

    @Transactional
    public void notify(User recipient, Announce ann, String message, NotificationType type) {
        Notification n = Notification.builder()
                .recipient(recipient)
                .announcement(ann)
                .type(type)
                .message(message)
                .seen(false)
                .build();
        n = repo.save(n);
        events.publishEvent(new NotificationCreatedEvent(n)); // push sera AFTER_COMMIT
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(NotificationCreatedEvent ev) {
        Notification n = ev.notif();
        NotificationDto dto = NotificationMapper.toDto(n);
        Long uid = n.getRecipient().getId_user();
        streamService.push(dto, uid);
    }

    @Transactional(readOnly = true)
    public List<Notification> listForUser(Long userId) {
        return repo.findByRecipientOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markSeen(String id) {
        var n = repo.findById(id).orElseThrow();
        n.setSeen(true);
        repo.save(n);
    }

    @Transactional
    public void markAllSeen(Long userId) {
        var list = repo.findByRecipientOrderByCreatedAtDesc(userId);
        for (var n : list) if (!n.isSeen()) n.setSeen(true);
        repo.saveAll(list);
    }

    @Transactional(readOnly = true)
    public long unseenCount(Long userId) {
        return repo.countUnseen(userId);
    }

}

//    private final NotificationRepository repo;
//    @Autowired
//    private NotificationStreamService streamService; // 👈 SSE
//
//    /**
//     * Crée et persiste une notification, puis la pousse en SSE
//     * uniquement après COMMIT pour éviter les "fantômes".
//     */
Fields:
  private final NotificationRepository repo
  private final ApplicationEventPublisher events
  private final NotificationStreamService streamService
  private final NotificationRepository repo
  private NotificationStreamService streamService


==== java/com/example/evaliaproject/service/RecompensesService.java ====
Class: RecompensesService (class)


==== java/com/example/evaliaproject/service/IQuestionService.java ====
Class: IQuestionService (interface)


==== java/com/example/evaliaproject/service/QuestionService.java ====
Class: QuestionService (class)
Fields:
  private QuestionRepository questionRepository


==== java/com/example/evaliaproject/service/PlanningService.java ====
Class: PlanningService (class)
Fields:
  private final PlanningRepository repo
  private final AnnouncementRepository announcementRepo
  private final UserRepository userRepo
  private final UserRepository userRepo
  private final NotificationService notificationService


==== java/com/example/evaliaproject/service/IResponsePanelisteService.java ====
Class: IResponsePanelisteService (interface)


==== java/com/example/evaliaproject/service/NotificationStreamService.java ====
Class: NotificationStreamService (class)
Javadoc:
/**
 * Gère les connexions SSE par utilisateur et l'envoi
 * de notifications aux clients connectés.
 */
@Service
public class NotificationStreamService {




    private final ConcurrentMap<Long, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private static final long TIMEOUT_MS = Duration.ofMinutes(30).toMillis();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emitters.computeIfAbsent(userId, id -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        try { emitter.send(SseEmitter.event().name("hello").data("connected")); }
        catch (IOException ignored) {}

        return emitter;
    }

    /** ⬇️ maintenant on push un DTO */
    public void push(NotificationDto dto, Long recipientUserId) {
        Set<SseEmitter> set = emitters.get(recipientUserId);
        if (set == null || set.isEmpty()) return;

        for (SseEmitter em : set) {
            try {
                em.send(
                        SseEmitter.event()
                                .name("notification")
                                .id(dto.id())
                                .data(dto)
                );
            } catch (IOException e) {
                remove(recipientUserId, em);
            }
        }
    }

    public void ping(Long userId) {
        Set<SseEmitter> set = emitters.get(userId);
        if (set == null) return;
        for (SseEmitter em : set) {
            try { em.send(SseEmitter.event().name("ping").data("•")); }
            catch (IOException ignored) {}
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        var set = emitters.get(userId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) emitters.remove(userId);
        }
    }

}
//
//    // Map: userId -> set d'emitters (un par onglet navigateur)
//    private final ConcurrentMap<Long, CopyOnWriteArraySet<SseEmitter>> emitters = new ConcurrentHashMap<>();
//
//    // Durée de vie d'une connexion SSE (le navigateur se reconnecte)
//    private static final long TIMEOUT_MS = Duration.ofMinutes(30).toMillis();
//
//    /**
//     * Appelé quand un client s'abonne au flux SSE (/notifications/stream).
//     */
//    public SseEmitter register(Long userId) {
//        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
//
//        emitters.computeIfAbsent(userId, id -> new CopyOnWriteArraySet<>()).add(emitter);
//
//        // Nettoyage quand la connection se termine/expire/erreur
//        emitter.onCompletion(() -> remove(userId, emitter));
//        emitter.onTimeout(() -> remove(userId, emitter));
//        emitter.onError(e -> remove(userId, emitter));
//
//        // message "hello" immédiat (facultatif)
//        try {
//            emitter.send(SseEmitter.event().name("hello").data("connected"));
//        } catch (IOException ignored) {}
//
//        return emitter;
//    }
//
//    /**
//     * Envoie une notification à tous les clients connectés du destinataire.
//     * Appeler ceci APRÈS COMMIT (voir NotificationService).
//     */
//    public void push(Notification n) {
//        Long uid = n.getRecipient().getId_user();
//        Set<SseEmitter> set = emitters.get(uid);
//        if (set == null || set.isEmpty()) return;
//
//        for (SseEmitter em : set) {
//            try {
//                em.send(SseEmitter.event()
//                        .name("notification")     // event name côté front
//                        .id(n.getIdnotif())       // permet "Last-Event-ID" si besoin
//                        .data(n));                // Spring convertit en JSON
//            } catch (IOException e) {
//                // si le client a fermé, on retire l'emitter
//                remove(uid, em);
//            }
//        }
//    }
//
//    /** Envoi d'un ping pour garder la connexion active (optionnel). */


==== java/com/example/evaliaproject/service/IFeedbackSevice.java ====
Class: IFeedbackSevice (interface)


==== java/com/example/evaliaproject/service/IQuizService.java ====
Class: IQuizService (interface)


==== java/com/example/evaliaproject/service/FeedbackService.java ====
Class: FeedbackService (class)
Fields:
  private FeedbackRepository feedbackRepository
  private AnnouncementRepository announcementRepository
  private UserRepository userRepository
  private EarnedRewardRepository earnedRewardRepository
  private NotificationService notificationService


==== java/com/example/evaliaproject/service/FileStorageService.java ====
Class: FileStorageService (class)
Fields:
  private String uploadDir


==== java/com/example/evaliaproject/service/ServiceEmail.java ====
Class: ServiceEmail (class)
Fields:
  private JavaMailSender mailSender
  private String from
  private String supportEmail
  private String loginUrl


==== java/com/example/evaliaproject/service/IUserService.java ====
Class: IUserService (interface)


==== java/com/example/evaliaproject/service/CategoryService.java ====
Class: CategoryService (class)
Fields:
  private CategoryRepository categoryRepository


==== java/com/example/evaliaproject/service/IAnnouncementService.java ====
Class: IAnnouncementService (interface)


==== java/com/example/evaliaproject/service/QuizFlowService.java ====
Class: QuizFlowService (class)
Fields:
  private final AnnouncementRepository announcementRepository
  private final QuizRepository quizRepository
  private final QuizAttemptRepository attemptRepository
  private final AttemptAnswerRepository answerRepository
  private final QuestionRepository questionRepository
  private final ResponsePanelisteRepository responseRepository
  private final UserRepository userRepository


==== java/com/example/evaliaproject/service/AnnouncementService.java ====
Class: AnnouncementService (class)
Fields:
  private AnnouncementRepository announcementRepository
  private CategoryRepository categoryRepository
  private UserRepository userRepository
  private RecompensesRepository recompensesRepository
  private QuestionRepository questionRepository
  private AttemptAnswerRepository attemptAnswerRepository
  private QuizAttemptRepository quizAttemptRepository
  private FeedbackRepository feedbackRepository
  private EarnedRewardRepository earnedRewardRepository
  private NotificationRepository notificationRepository
  private String uploadDir


==== java/com/example/evaliaproject/service/ReclamationService.java ====
Class: ReclamationService (class)


==== java/com/example/evaliaproject/service/UserService.java ====
Class: UserService (class)
Fields:
  private UserRepository userRepository


==== java/com/example/evaliaproject/service/IRecompensesService.java ====
Class: IRecompensesService (interface)


==== java/com/example/evaliaproject/service/IReclamationService.java ====
Class: IReclamationService (interface)


==== java/com/example/evaliaproject/config/JwtAuthentificationFilter.java ====
Class: JwtAuthentificationFilter (class)
Fields:
  private final UserDetailsService userDetailsService
  private final JwtService jwtService


==== java/com/example/evaliaproject/config/ApplicationConfig.java ====
Class: ApplicationConfig (class)
Fields:
  private final PasswordEncoder passwordEncoder
  private final UserRepository userRepository


==== java/com/example/evaliaproject/config/SseNoBufferFilter.java ====
Class: SseNoBufferFilter (class)
Javadoc:
/**
 * Désactive le buffering proxy pour les réponses SSE afin
 * d'éviter des retards d'affichage côté client.
 */


==== java/com/example/evaliaproject/config/OsivConfig.java ====
Class: OsivConfig (class)


==== java/com/example/evaliaproject/config/JwtService.java ====
Class: JwtService (class)


==== java/com/example/evaliaproject/config/SecurityConfiguration.java ====
Class: SecurityConfiguration (class)
Fields:
  private final JwtAuthentificationFilter jwtAuthFilter
  private final AuthenticationProvider authenticationProvider


==== java/com/example/evaliaproject/repository/AttemptAnswerRepository.java ====
Class: AttemptAnswerRepository (interface)


==== java/com/example/evaliaproject/repository/QuizRepository.java ====
Class: QuizRepository (interface)


==== java/com/example/evaliaproject/repository/QuizAttemptRepository.java ====
Class: QuizAttemptRepository (interface)


==== java/com/example/evaliaproject/repository/ReclamationRepository.java ====
Class: ReclamationRepository (interface)


==== java/com/example/evaliaproject/repository/NotificationRepository.java ====
Class: NotificationRepository (interface)


==== java/com/example/evaliaproject/repository/RecompensesRepository.java ====
Class: RecompensesRepository (interface)


==== java/com/example/evaliaproject/repository/QuestionRepository.java ====
Class: QuestionRepository (interface)


==== java/com/example/evaliaproject/repository/FeedbackRepository.java ====
Class: FeedbackRepository (interface)


==== java/com/example/evaliaproject/repository/RoleRepository.java ====
Class: RoleRepository (interface)


==== java/com/example/evaliaproject/repository/ResponsePanelisteRepository.java ====
Class: ResponsePanelisteRepository (interface)


==== java/com/example/evaliaproject/repository/EarnedRewardRepository.java ====
Class: EarnedRewardRepository (interface)


==== java/com/example/evaliaproject/repository/TokenRepository.java ====
Class: TokenRepository (interface)


==== java/com/example/evaliaproject/repository/CategoryRepository.java ====
Class: CategoryRepository (interface)


==== java/com/example/evaliaproject/repository/AnnouncementRepository.java ====
Class: AnnouncementRepository (interface)


==== java/com/example/evaliaproject/repository/PlanningRepository.java ====
Class: PlanningRepository (interface)


==== java/com/example/evaliaproject/repository/UserRepository.java ====
Class: UserRepository (interface)


==== java/com/example/evaliaproject/auth/RestExceptionHandler.java ====
Class: RestExceptionHandler (class)


==== java/com/example/evaliaproject/auth/AuthenticationService.java ====
Class: AuthenticationService (class)
Fields:
  private final UserRepository userRepository
  private final RoleRepository roleRepository
  private final JwtService jwtService
  private final TokenRepository tokenRepository
  private final ServiceEmail serviceEmail
  private final FileStorageService fileStorageService
  private Token token
  private final PasswordEncoder passwordEncoder
  private final AuthenticationManager authenticationManager


==== java/com/example/evaliaproject/auth/AuthenticationResponse.java ====
Class: AuthenticationResponse (class)
Fields:
  private String token
  private String message
  private boolean pending


==== java/com/example/evaliaproject/auth/OtpLoginController.java ====
Class: OtpLoginController (class)
Class: ConfirmOtpRequest (class)
Mappings:
  @RequestMapping paths=['/api/v1/auth']
  @PostMapping paths=['/confirm-otp']
Fields:
  private final UserRepository userRepository
  private final TokenRepository tokenRepository
  private final JwtService jwtService
  private String email
  private String code


==== java/com/example/evaliaproject/auth/AuthenticationRequest.java ====
Class: AuthenticationRequest (class)
Fields:
  private String email
  private String password


==== java/com/example/evaliaproject/auth/PasswordResetService.java ====
Class: PasswordResetService (class)
Fields:
  private final UserRepository userRepository
  private final TokenRepository tokenRepository
  private final PasswordEncoder passwordEncoder
  private final ServiceEmail email
  private String frontendUrl


==== java/com/example/evaliaproject/auth/RegisterRequest.java ====
Class: RegisterRequest (class)
Fields:
  private String firstname
  private String lastname
  private String deliveryAddress
  private String email
  private String password
  private String numTelephone
  private TypeUser typeUser
  private String companyName
  private String jobTitle
  private Integer age
  private String ageRange
  private String iban
  private String role
  private String firstname
  private String lastname
  private String email
  private String password
  private String role
  private String ageRange
  private LocalDateTime createdDate


==== java/com/example/evaliaproject/auth/PasswordResetController.java ====
Class: PasswordResetController (class)
Class: ForgotRequest (class)
Class: ResetRequest (class)
Mappings:
  @RequestMapping paths=['/api/v1/auth/password']
  @PostMapping paths=['/forgot']
  @PostMapping paths=['/reset']
Fields:
  private final PasswordResetService resetService
  private String email
  private String code
  private String newPassword


==== java/com/example/evaliaproject/auth/AuthenticationController.java ====
Class: AuthenticationController (class)
Class: FirstLoginOtpRequest (class)
Mappings:
  @RequestMapping paths=['/api/v1/auth']
  @GetMapping paths=['/getPanelist']
  @GetMapping paths=['/getPanelist/{id}']
  @PostMapping paths=['/authenticate/first-login/verify