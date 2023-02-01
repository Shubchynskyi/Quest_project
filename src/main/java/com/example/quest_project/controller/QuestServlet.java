package com.example.quest_project.controller;

import com.example.quest_project.entity.Quest;
import com.example.quest_project.entity.Question;
import com.example.quest_project.service.ImageService;
import com.example.quest_project.service.QuestService;
import com.example.quest_project.service.QuestionService;
import com.example.quest_project.util.Go;
import com.example.quest_project.util.Jsp;
import com.example.quest_project.util.Key;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


@WebServlet(name = "QuestServlet", value = Go.QUEST)
public class QuestServlet extends HttpServlet {

    QuestService questService = QuestService.QUEST_SERVICE;
    QuestionService questionService = QuestionService.QUESTION_SERVICE;
    ImageService imageService = ImageService.IMAGE_SERVICE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Quest> questOptional = questService.get(request.getParameter(Key.ID));

        if(questOptional.isPresent()) {
            Quest quest = questOptional.get();
            Map<String, String[]> parameterMap = request.getParameterMap();
            if(parameterMap.containsKey("question")) {
                String questionId = request.getParameter("question");
                Optional<Question> questionOptional = questionService.get(questionId);
                if(questionOptional.isPresent()) {
                    Question question = questionOptional.get();
                    request.setAttribute("question", question);
                }
            } else {
                request.setAttribute("startQuestionId", quest.getStartQuestionId());
                request.setAttribute("questDescription", quest.getDescription());
            }

            request.setAttribute(Key.ID, quest.getId());
            request.setAttribute("questName", quest.getName());


            Jsp.forward(request, response, Key.QUEST);
        } else {
            //редирект на список квестов
            Jsp.redirect(response, Key.QUESTS_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // тут могу получить questId / questionId / answerId после первого Post метода

        // 1) если есть вопрос, то передаю дальше имя квеста и вопрос (это будет первым вопросом квеста)
        // 2) если есть ответ, то передаю дальше имя квеста и следующий вопрос который получаю из ответа

        // переместить, возможно при редиректе эти данные уже будут
        String questId = request.getParameter(Key.ID);
        String questName = request.getParameter("questName");

        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.containsKey("nextQuestionId")) {
            String nextQuestionId = request.getParameter("nextQuestionId");
            Optional<Question> nextQuestionOptional = questionService.get(nextQuestionId);
            if(nextQuestionOptional.isPresent()) {
                Question question = nextQuestionOptional.get();
                request.setAttribute("question", question);
                request.setAttribute(Key.ID, questId);
                request.setAttribute("questName", questName);
                String newUrl = "%s?%s=%s&%s=%d".formatted(Key.QUEST, Key.ID, questId, "question", question.getId());
                Jsp.redirect(response, newUrl);
            } else {
                Jsp.redirect(response, Key.QUESTS_LIST);
            }
            //TODO собрать url
        } else {

            String questionId = request.getParameter("questionId");
//            надо ли получать квест? Сохраняются ли в запросе прежние параметры?
//            Optional<Quest> questOptional = questService.get(questId);
            Optional<Question> questionOptional = questionService.get(questionId);

            if(questionOptional.isPresent()) {
//                Quest quest = questOptional.get();
                Question question = questionOptional.get();
                request.setAttribute(Key.ID, questId);
                request.setAttribute("questName", questName);
                request.setAttribute(Key.QUESTION, question);
                Jsp.forward(request, response, Key.QUEST);
            } else {
                //редирект на список квестов
                Jsp.redirect(response, Key.QUESTS_LIST);
            }

        }






    }
}
