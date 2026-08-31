package com.mediinbusan.app.core.i18n

data class InfoSectionText(val heading: String, val body: String)

data class InfoContentText(val intro: String, val sections: List<InfoSectionText>)

/**
 * Settings(S-10) "정보" 섹션의 이용안내/개인정보처리방침/이용약관 3종 상세 본문.
 * 제목(usageGuideTitle 등)은 SettingsStrings와 중복되지 않도록 여기 두지 않고 재사용한다.
 */
data class SettingsInfoDetailStrings(
    val usageGuide: InfoContentText,
    val privacyPolicy: InfoContentText,
    val terms: InfoContentText
) {
    companion object {
        val Ko = SettingsInfoDetailStrings(
            usageGuide = InfoContentText(
                intro = "메디인부산(MediIn Busan)은 부산을 방문하는 외국인 의료관광객이 자신의 의료 목적에 맞는 부산 의료기관을 탐색하고, 의료 이용 절차와 병원 주변 관광·웰니스 정보를 함께 확인할 수 있도록 돕는 정보 제공형 서비스입니다.",
                sections = listOf(
                    InfoSectionText(
                        "1. 의료기관 찾기",
                        "홈 화면에서 의료 목적(피부·미용, 건강검진, 치과, 한방, 재활 등)을 선택하면 관련 의료기관 목록을 볼 수 있습니다. 목록에서 병원을 선택하면 위치, 진료과목, 지원 언어, 이용 가능 시간 등 상세 정보를 확인할 수 있습니다."
                    ),
                    InfoSectionText(
                        "2. 의료 이용 가이드",
                        "의료기관 상세 화면에서 '의료 이용 가이드'를 통해 예약 문의부터 진료, 귀국까지 일반적인 이용 절차 안내를 확인할 수 있습니다."
                    ),
                    InfoSectionText(
                        "3. 주변 관광·웰니스 정보",
                        "의료기관 주변의 관광지, 음식점, 웰니스 장소 정보를 함께 확인하여 의료 목적 외 일정도 계획할 수 있습니다."
                    ),
                    InfoSectionText(
                        "4. 즐겨찾기",
                        "관심 있는 의료기관이나 장소를 즐겨찾기에 저장해 두고 나중에 다시 확인할 수 있습니다."
                    ),
                    InfoSectionText(
                        "5. 제공하지 않는 기능",
                        "메디인부산은 정보 제공에 집중하는 서비스로, 병원 예약 대행·진료비 결제·실시간 상담 또는 통역사 매칭·사용자의 실시간 위치를 이용한 위치기반 추천은 제공하지 않습니다.\n\n실제 진료 예약, 비용, 통역 서비스는 각 의료기관의 공식 채널을 통해 직접 문의해 주시기 바랍니다."
                    )
                )
            ),
            privacyPolicy = InfoContentText(
                intro = "메디인부산(이하 '서비스')은 이용자의 개인정보를 소중히 다루며, 관련 법령을 준수하기 위해 노력합니다.",
                sections = listOf(
                    InfoSectionText(
                        "수집하는 개인정보 항목",
                        "서비스는 회원가입 절차 없이 이용 가능하며, 선택한 언어 설정·의료 목적 카테고리·즐겨찾기 및 최근 본 항목 목록은 이용자의 기기 내부에만 저장되고 서비스 서버로 전송되지 않습니다.\n\n다만 다음 두 기능을 이용하는 경우에는 입력·촬영한 내용이 처리 목적으로 외부에 전송됩니다.\n· AI 준비체크(챗봇) 대화창에 입력한 내용은 응답 생성을 위해 당사 서버를 거쳐 AI 처리업체(Google Gemini API)로 전송되어 처리됩니다.\n· 문서 스캔 기능에서 촬영하거나 선택한 이미지와 인식된 텍스트는 문자 인식 및 번역을 위해 당사 서버를 거쳐 처리업체(네이버클라우드 CLOVA OCR, Papago 번역)로 전송되어 처리됩니다."
                    ),
                    InfoSectionText(
                        "수집하지 않는 정보",
                        "서비스는 이용자의 실시간 위치 정보(GPS), 결제 정보, 이름·연락처 등 본인 식별 정보를 수집하지 않습니다."
                    ),
                    InfoSectionText(
                        "개인정보의 이용 목적",
                        "저장된 언어·의료목적 설정은 다음 방문 시 동일한 환경을 제공하기 위한 목적으로만 사용됩니다. AI 준비체크 대화 내용과 문서 스캔 이미지·텍스트는 각각 준비 유형 안내 생성, 문서 텍스트 인식·번역이라는 처리 목적으로만 일시적으로 사용됩니다."
                    ),
                    InfoSectionText(
                        "개인정보의 보유 및 파기",
                        "기기 내부에 저장된 정보는 이용자가 앱을 삭제하거나 설정에서 직접 초기화할 때까지 보관되며, 별도의 서버에 보관되지 않으므로 앱 삭제 시 함께 파기됩니다. AI 준비체크 대화 내용과 문서 스캔 이미지·인식 텍스트는 당사 서버에 별도로 저장하지 않고 처리 즉시 폐기합니다."
                    ),
                    InfoSectionText(
                        "제3자 제공",
                        "서비스는 이용자의 정보를 원칙적으로 제3자에게 제공하지 않습니다. 다만 위 'AI 준비체크'와 '문서 스캔' 기능을 이용하면 입력·촬영한 내용이 해당 기능의 처리를 위해 다음 업체로 전송됩니다: Google(Gemini API, 대화 응답 생성), 네이버클라우드(CLOVA OCR·Papago 번역, 문자 인식 및 번역). 전송된 내용은 각 업체가 처리 목적 달성 후 자체 정책에 따라 처리하며, 당사 서버에는 별도로 저장되지 않습니다. 그 외 의료기관·관광 정보는 한국관광공사가 제공하는 공공데이터(OpenAPI)를 통해 조회됩니다."
                    ),
                    InfoSectionText(
                        "문의처",
                        "개인정보 관련 문의사항은 고객센터(support@medinbusan.kr)로 연락해 주시기 바랍니다."
                    )
                )
            ),
            terms = InfoContentText(
                intro = "이 약관은 메디인부산(이하 '서비스')이 제공하는 정보 제공 서비스의 이용과 관련하여 서비스와 이용자 간의 권리, 의무 및 책임사항을 정함을 목적으로 합니다.",
                sections = listOf(
                    InfoSectionText(
                        "제1조 (서비스의 내용)",
                        "서비스는 부산 지역 의료기관 및 주변 관광·웰니스 정보를 제공하는 정보 제공형 서비스입니다. 서비스는 의료 자문, 진단 또는 처방을 제공하지 않으며, 병원 예약 대행이나 결제 기능을 포함하지 않습니다."
                    ),
                    InfoSectionText(
                        "제2조 (이용자의 의무)",
                        "이용자는 서비스에서 제공하는 정보를 참고 자료로만 활용해야 하며, 실제 의료기관 방문 전 반드시 해당 기관의 공식 채널을 통해 최신 정보를 확인해야 합니다."
                    ),
                    InfoSectionText(
                        "제3조 (면책조항)",
                        "서비스가 제공하는 의료기관·관광 정보는 공공데이터를 기반으로 하며, 정보의 변경 시점에 따라 실제와 차이가 있을 수 있습니다. 서비스는 정보의 정확성을 위해 노력하지만, 정보의 최신성이나 완전성을 보증하지 않으며 이용자가 정보를 신뢰하여 발생한 손해에 대해 책임을 지지 않습니다."
                    ),
                    InfoSectionText(
                        "제4조 (저작권)",
                        "서비스에서 제공하는 콘텐츠에 대한 저작권은 서비스 또는 각 정보 제공기관(한국관광공사 등)에 있으며, 이용자는 이를 무단으로 복제, 배포할 수 없습니다."
                    ),
                    InfoSectionText(
                        "제5조 (약관의 변경)",
                        "서비스는 필요한 경우 관련 법령을 준수하는 범위 내에서 이 약관을 변경할 수 있으며, 변경된 약관은 서비스 내 공지를 통해 안내합니다."
                    )
                )
            )
        )

        val En = SettingsInfoDetailStrings(
            usageGuide = InfoContentText(
                intro = "MediIn Busan is an information service that helps foreign medical tourists visiting Busan explore Busan hospitals matching their medical purpose, and check the medical process along with tourism and wellness information near the hospital.",
                sections = listOf(
                    InfoSectionText(
                        "1. Finding a hospital",
                        "Choose a medical purpose (Skin & Beauty, Health Checkup, Dental, Oriental Medicine, Rehabilitation, etc.) on the home screen to see a list of related hospitals. Selecting a hospital shows details such as location, specialties, supported languages, and available hours."
                    ),
                    InfoSectionText(
                        "2. Medical guide",
                        "From the hospital detail screen, use 'Medical guide' to see a general walkthrough of the process from booking inquiries through treatment to returning home."
                    ),
                    InfoSectionText(
                        "3. Nearby tourism & wellness",
                        "Check tourist attractions, restaurants, and wellness spots near the hospital together, so you can plan activities beyond your medical purpose."
                    ),
                    InfoSectionText(
                        "4. Favorites",
                        "Save hospitals or places you're interested in to Favorites so you can check them again later."
                    ),
                    InfoSectionText(
                        "5. Features not provided",
                        "MediIn Busan focuses on providing information and does not offer hospital booking on your behalf, payment for treatment costs, real-time consultation or interpreter matching, or location-based recommendations using your real-time location.\n\nPlease contact each hospital's official channel directly for actual appointment booking, costs, and interpretation services."
                    )
                )
            ),
            privacyPolicy = InfoContentText(
                intro = "MediIn Busan ('the Service') values users' personal information and strives to comply with relevant laws.",
                sections = listOf(
                    InfoSectionText(
                        "Personal information collected",
                        "The Service can be used without a sign-up process. Your selected language setting, medical purpose category, and favorites/recently viewed list are stored only on your device and are not transmitted to the Service's servers.\n\nHowever, using the following two features does send what you enter or capture to outside processors for that purpose:\n· Text you enter in the AI Prep Check (chatbot) conversation is sent through our server to an AI processor (Google Gemini API) to generate a reply.\n· Images you capture or select in the document scan feature, and the text recognized from them, are sent through our server to processors (Naver Cloud CLOVA OCR, Papago Translation) for text recognition and translation."
                    ),
                    InfoSectionText(
                        "Information not collected",
                        "The Service does not collect your real-time location (GPS), payment information, or identifying information such as your name or contact details."
                    ),
                    InfoSectionText(
                        "Purpose of use",
                        "The stored language and medical purpose settings are used only to provide the same environment on your next visit. AI Prep Check conversation text and document scan images/text are used only temporarily, for the sole purpose of generating preparation-type guidance and recognizing/translating document text, respectively."
                    ),
                    InfoSectionText(
                        "Retention and destruction",
                        "Information stored on the device is kept until you delete the app or reset it directly in Settings, and is not stored on any separate server, so it is destroyed together when the app is deleted. AI Prep Check conversation text and document scan images/recognized text are not separately stored on our servers and are discarded immediately after processing."
                    ),
                    InfoSectionText(
                        "Disclosure to third parties",
                        "The Service does not disclose your information to third parties as a general rule. However, using the 'AI Prep Check' and 'document scan' features above sends what you enter or capture to the following processors for that feature's processing: Google (Gemini API, to generate chat replies) and Naver Cloud (CLOVA OCR / Papago Translation, for text recognition and translation). Once each processor has served its purpose, it handles the data under its own policy, and it is not separately stored on our servers. Aside from this, hospital and tourism information is retrieved via public data (OpenAPI) provided by the Korea Tourism Organization."
                    ),
                    InfoSectionText(
                        "Contact",
                        "For inquiries about personal information, please contact our support center (support@medinbusan.kr)."
                    )
                )
            ),
            terms = InfoContentText(
                intro = "These terms set out the rights, obligations, and responsibilities between the Service and users regarding use of the information service provided by MediIn Busan ('the Service').",
                sections = listOf(
                    InfoSectionText(
                        "Article 1 (Service content)",
                        "The Service is an information service providing information on hospitals in the Busan area and nearby tourism/wellness spots. The Service does not provide medical advice, diagnosis, or prescriptions, and does not include hospital booking on your behalf or payment features."
                    ),
                    InfoSectionText(
                        "Article 2 (User obligations)",
                        "Users must use the information provided by the Service as reference material only, and must verify the latest information through the relevant institution's official channel before actually visiting a hospital."
                    ),
                    InfoSectionText(
                        "Article 3 (Disclaimer)",
                        "Hospital and tourism information provided by the Service is based on public data and may differ from reality depending on when it was last updated. The Service strives for accuracy but does not guarantee the timeliness or completeness of the information, and is not liable for damages arising from reliance on the information."
                    ),
                    InfoSectionText(
                        "Article 4 (Copyright)",
                        "Copyright in content provided by the Service belongs to the Service or the respective information providers (such as the Korea Tourism Organization), and users may not reproduce or distribute it without authorization."
                    ),
                    InfoSectionText(
                        "Article 5 (Changes to terms)",
                        "The Service may change these terms when necessary, within the scope of compliance with relevant laws, and will announce any changed terms through in-service notices."
                    )
                )
            )
        )

        val Zh = SettingsInfoDetailStrings(
            usageGuide = InfoContentText(
                intro = "MediIn Busan 是一项信息提供型服务，帮助来访釜山的外国医疗旅游者查找符合自身医疗目的的釜山医疗机构，并同时了解医疗利用流程及医院周边的观光·养生信息。",
                sections = listOf(
                    InfoSectionText(
                        "1. 寻找医疗机构",
                        "在主页选择医疗目的（皮肤美容、健康体检、牙科、韩医、康复等）即可查看相关医疗机构列表。在列表中选择医院后，可查看位置、诊疗科目、支持语言、可利用时间等详细信息。"
                    ),
                    InfoSectionText(
                        "2. 医疗利用指南",
                        "在医院详情页面中，可通过“医疗利用指南”查看从预约咨询到就诊、回国为止的一般利用流程说明。"
                    ),
                    InfoSectionText(
                        "3. 周边观光·养生信息",
                        "可一并查看医院周边的观光景点、餐厅、养生场所信息，从而规划医疗目的以外的行程。"
                    ),
                    InfoSectionText(
                        "4. 收藏",
                        "可将感兴趣的医疗机构或场所保存至收藏，以便日后再次查看。"
                    ),
                    InfoSectionText(
                        "5. 不提供的功能",
                        "MediIn Busan 是专注于信息提供的服务，不提供医院预约代办、诊疗费用支付、实时咨询或口译人员匹配、基于用户实时位置的位置推荐等功能。\n\n实际的诊疗预约、费用、口译服务，请直接通过各医疗机构的官方渠道咨询。"
                    )
                )
            ),
            privacyPolicy = InfoContentText(
                intro = "MediIn Busan（以下简称“本服务”）重视用户的个人信息，并努力遵守相关法律法规。",
                sections = listOf(
                    InfoSectionText(
                        "收集的个人信息项目",
                        "本服务无需注册即可使用，您选择的语言设置、医疗目的类别、收藏及最近浏览列表仅保存在您的设备中，不会传输至本服务的服务器。\n\n但使用以下两项功能时，您输入或拍摄的内容会为处理目的传输至外部。\n· 在“AI准备检查”（聊天机器人）对话窗口中输入的内容，会经由本公司服务器传输至AI处理商（Google Gemini API）以生成回复。\n· 在文档扫描功能中拍摄或选择的图片及识别出的文字，会经由本公司服务器传输至处理商（Naver Cloud CLOVA OCR、Papago翻译）以进行文字识别与翻译。"
                    ),
                    InfoSectionText(
                        "不收集的信息",
                        "本服务不收集用户的实时位置信息（GPS）、支付信息、姓名·联系方式等身份识别信息。"
                    ),
                    InfoSectionText(
                        "个人信息的使用目的",
                        "保存的语言·医疗目的设置仅用于在下次访问时提供相同的使用环境。AI准备检查对话内容及文档扫描图片·文字，分别仅出于生成准备类型指南、识别与翻译文档文字的处理目的被临时使用。"
                    ),
                    InfoSectionText(
                        "个人信息的保留及销毁",
                        "保存在设备内部的信息将保留至用户删除应用或在设置中直接重置为止，且不会保存在独立服务器中，因此在删除应用时会一并销毁。AI准备检查对话内容及文档扫描图片·识别文字不会另外保存在本公司服务器中，处理完成后立即销毁。"
                    ),
                    InfoSectionText(
                        "向第三方提供",
                        "本服务原则上不会向第三方提供用户信息。但使用上述“AI准备检查”与“文档扫描”功能时，您输入或拍摄的内容会为该功能的处理传输至以下企业：Google（Gemini API，用于生成对话回复）、Naver Cloud（CLOVA OCR·Papago翻译，用于文字识别及翻译）。传输的内容在达成处理目的后由各企业按其自身政策处理，不会另外保存在本公司服务器中。除此之外，医疗机构·观光信息是通过韩国观光公社提供的公共数据（OpenAPI）进行查询的。"
                    ),
                    InfoSectionText(
                        "咨询方式",
                        "如有个人信息相关咨询事项，请联系客服中心（support@medinbusan.kr）。"
                    )
                )
            ),
            terms = InfoContentText(
                intro = "本条款旨在规定 MediIn Busan（以下简称“本服务”）所提供的信息服务在使用过程中，本服务与用户之间的权利、义务及责任事项。",
                sections = listOf(
                    InfoSectionText(
                        "第1条（服务内容）",
                        "本服务是提供釜山地区医疗机构及周边观光·养生信息的信息提供型服务。本服务不提供医疗咨询、诊断或处方，也不包含医院预约代办或支付功能。"
                    ),
                    InfoSectionText(
                        "第2条（用户义务）",
                        "用户应仅将本服务提供的信息作为参考资料使用，实际前往医疗机构前必须通过该机构的官方渠道确认最新信息。"
                    ),
                    InfoSectionText(
                        "第3条（免责条款）",
                        "本服务提供的医疗机构·观光信息基于公共数据，可能因信息更新时点不同而与实际情况存在差异。本服务努力保证信息的准确性，但不保证信息的时效性或完整性，对用户因信赖该信息而产生的损失不承担责任。"
                    ),
                    InfoSectionText(
                        "第4条（著作权）",
                        "本服务提供内容的著作权归本服务或各信息提供机构（如韩国观光公社等）所有，用户不得擅自复制、传播。"
                    ),
                    InfoSectionText(
                        "第5条（条款的变更）",
                        "本服务在必要时可在遵守相关法律法规的范围内变更本条款，变更后的条款将通过服务内公告进行说明。"
                    )
                )
            )
        )

        val Ja = SettingsInfoDetailStrings(
            usageGuide = InfoContentText(
                intro = "メディインブサン（MediIn Busan）は、釜山を訪れる外国人医療観光客が自身の医療目的に合った釜山の医療機関を探し、医療利用の手続きや病院周辺の観光・ウェルネス情報を併せて確認できるよう支援する情報提供型サービスです。",
                sections = listOf(
                    InfoSectionText(
                        "1. 医療機関を探す",
                        "ホーム画面で医療目的（皮膚・美容、健康診断、歯科、韓方、リハビリなど）を選択すると、関連する医療機関の一覧を確認できます。一覧から病院を選択すると、位置、診療科目、対応言語、利用可能時間などの詳細情報を確認できます。"
                    ),
                    InfoSectionText(
                        "2. 医療利用ガイド",
                        "医療機関詳細画面の「医療利用ガイド」から、予約問い合わせから診療、帰国までの一般的な利用手順のご案内を確認できます。"
                    ),
                    InfoSectionText(
                        "3. 周辺の観光・ウェルネス情報",
                        "医療機関周辺の観光地、飲食店、ウェルネススポットの情報も併せて確認し、医療目的以外の日程も計画できます。"
                    ),
                    InfoSectionText(
                        "4. お気に入り",
                        "気になる医療機関やスポットをお気に入りに保存しておき、後でもう一度確認できます。"
                    ),
                    InfoSectionText(
                        "5. 提供していない機能",
                        "メディインブサンは情報提供に特化したサービスであり、病院予約の代行・診療費の決済・リアルタイム相談や通訳者マッチング・ユーザーのリアルタイム位置情報を利用した位置情報ベースのおすすめは提供していません。\n\n実際の診療予約、費用、通訳サービスについては、各医療機関の公式チャネルへ直接お問い合わせください。"
                    )
                )
            ),
            privacyPolicy = InfoContentText(
                intro = "メディインブサン（以下「本サービス」）は、利用者の個人情報を大切に扱い、関連法令を遵守するよう努めます。",
                sections = listOf(
                    InfoSectionText(
                        "収集する個人情報項目",
                        "本サービスは会員登録手続きなしで利用可能で、選択した言語設定・医療目的カテゴリー・お気に入りおよび最近見た項目リストは端末内部にのみ保存され、サービスのサーバーには送信されません。\n\nただし、次の2つの機能を利用する場合は、入力・撮影した内容が処理目的で外部に送信されます。\n・「AI準備チェック」（チャットボット）の会話欄に入力した内容は、応答生成のため当社サーバーを経由してAI処理事業者（Google Gemini API）に送信され処理されます。\n・書類スキャン機能で撮影または選択した画像および認識されたテキストは、文字認識と翻訳のため当社サーバーを経由して処理事業者（NAVER Cloud CLOVA OCR、Papago翻訳）に送信され処理されます。"
                    ),
                    InfoSectionText(
                        "収集しない情報",
                        "本サービスは利用者のリアルタイム位置情報（GPS）、決済情報、氏名・連絡先などの本人識別情報を収集しません。"
                    ),
                    InfoSectionText(
                        "個人情報の利用目的",
                        "保存された言語・医療目的の設定は、次回訪問時に同じ環境を提供する目的にのみ使用されます。AI準備チェックの会話内容および書類スキャンの画像・テキストは、それぞれ準備タイプ案内の生成、書類テキストの認識・翻訳という処理目的にのみ一時的に使用されます。"
                    ),
                    InfoSectionText(
                        "個人情報の保有および破棄",
                        "端末内部に保存された情報は、利用者がアプリを削除するか設定で直接初期化するまで保管され、別途サーバーには保管されないため、アプリ削除時に併せて破棄されます。AI準備チェックの会話内容および書類スキャンの画像・認識テキストは当社サーバーに別途保存されず、処理後直ちに破棄されます。"
                    ),
                    InfoSectionText(
                        "第三者提供",
                        "本サービスは原則として利用者の情報を第三者に提供しません。ただし、上記の「AI準備チェック」および「書類スキャン」機能を利用すると、入力・撮影した内容が当該機能の処理のため次の事業者に送信されます：Google（Gemini API、会話応答の生成）、NAVER Cloud（CLOVA OCR・Papago翻訳、文字認識および翻訳）。送信された内容は各事業者が処理目的の達成後、自社の方針に従って取り扱い、当社サーバーには別途保存されません。それ以外の医療機関・観光情報は、韓国観光公社が提供する公共データ（OpenAPI）を通じて照会されます。"
                    ),
                    InfoSectionText(
                        "お問い合わせ先",
                        "個人情報に関するお問い合わせは、カスタマーセンター（support@medinbusan.kr）までご連絡ください。"
                    )
                )
            ),
            terms = InfoContentText(
                intro = "本規約は、メディインブサン（以下「本サービス」）が提供する情報提供サービスの利用に関し、本サービスと利用者との間の権利、義務および責任事項を定めることを目的とします。",
                sections = listOf(
                    InfoSectionText(
                        "第1条（サービスの内容）",
                        "本サービスは、釜山地域の医療機関および周辺の観光・ウェルネス情報を提供する情報提供型サービスです。本サービスは医療に関する助言、診断または処方を提供せず、病院予約の代行や決済機能も含みません。"
                    ),
                    InfoSectionText(
                        "第2条（利用者の義務）",
                        "利用者は本サービスが提供する情報を参考資料としてのみ活用しなければならず、実際に医療機関を訪問する前に必ず当該機関の公式チャネルを通じて最新情報を確認する必要があります。"
                    ),
                    InfoSectionText(
                        "第3条（免責事項）",
                        "本サービスが提供する医療機関・観光情報は公共データに基づいており、情報の更新時点によって実際と差異が生じる場合があります。本サービスは情報の正確性のために努めますが、情報の最新性や完全性を保証するものではなく、利用者が情報を信頼したことにより発生した損害について責任を負いません。"
                    ),
                    InfoSectionText(
                        "第4条（著作権）",
                        "本サービスが提供するコンテンツに関する著作権は、本サービスまたは各情報提供機関（韓国観光公社など）に帰属し、利用者はこれを無断で複製、配布することはできません。"
                    ),
                    InfoSectionText(
                        "第5条（規約の変更）",
                        "本サービスは必要な場合、関連法令を遵守する範囲内で本規約を変更することができ、変更された規約はサービス内の告知を通じてご案内します。"
                    )
                )
            )
        )
    }
}
