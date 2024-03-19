/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.springaidemo;

import com.example.springaidemo.services.AnswerCorrectnessResponse;
import com.example.springaidemo.services.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
public class BasicEvaluationTest {

	private static final Logger logger = LoggerFactory.getLogger(BasicEvaluationTest.class);

	@Autowired
	protected ChatClient openAiChatClient;

	@Autowired
	protected QuestionService questionService;

	@Autowired
	protected OpenAiEmbeddingClient embeddingClient;

	@Value("classpath:/prompts/spring/test/evaluation/qa-evaluator-accurate-answer.st")
	protected Resource qaEvaluatorAccurateAnswerResource;

	@Value("classpath:/prompts/spring/test/evaluation/qa-evaluator-not-related-message.st")
	protected Resource qaEvaluatorNotRelatedResource;

	@Value("classpath:/prompts/spring/test/evaluation/qa-evaluator-answer-correctness.st")
	protected Resource qaEvaluatorFactBasedAnswerResource;

	@Value("classpath:/prompts/spring/test/evaluation/user-evaluator-message.st")
	protected Resource userEvaluatorResource;

	protected void evaluateQuestionAndAnswer(String question, String answer, boolean factBased) throws JsonProcessingException {
		assertThat(answer).isNotNull();
		logger.info("Question: " + question);
		logger.info("Answer:" + answer);
		String ground_truth = "Text: On the day of discharge from an inpatient admission, the Community Connect TCM agency will ensure the individual accesses a 30-day supply of medically necessary medication to ensure continuity of this aspect of treatment and medication adherence.";
		PromptTemplate userPromptTemplate = new PromptTemplate(userEvaluatorResource,
				Map.of("question", question, "answer", answer));
		Message systemMessage;
		if (factBased) {
//			systemMessage = new SystemMessage(qaEvaluatorFactBasedAnswerResource);
			PromptTemplate systemPromptTemplate = new SystemPromptTemplate(qaEvaluatorFactBasedAnswerResource);
			systemMessage = systemPromptTemplate.createMessage(
			Map.of(
			"question", question,
			"answer", answer,
			"ground_truth", ground_truth
			));
		}
		else {
			systemMessage = new SystemMessage(qaEvaluatorAccurateAnswerResource);
		}
		Message userMessage = userPromptTemplate.createMessage();
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
		String response = openAiChatClient.call(prompt).getResult().getOutput().getContent();
		logger.info("Answer is" + response);

		ObjectMapper objectMapper = new ObjectMapper();

		// Convert string to JsonNode
		AnswerCorrectnessResponse answerCorrectnessResponse = objectMapper.readValue(response, AnswerCorrectnessResponse.class);

		int tp = answerCorrectnessResponse.getTP().length;
		int fp = answerCorrectnessResponse.getFP().length;
		int fn = answerCorrectnessResponse.getFN().length;

		double simple_score = tp > 0 ? tp / (tp + 0.5 * (fp + fn)) : 0;

		//Calculate similarity score, based on the embedding of answer and ground_truth
		List<Double> embedding_1 = embeddingClient.embed(ground_truth);
		List<Double> embedding_2 = embeddingClient.embed(answer);


		RealVector vector1 = new ArrayRealVector(embedding_1.stream().mapToDouble(Double::doubleValue).toArray());
		RealVector vector2 = new ArrayRealVector(embedding_2.stream().mapToDouble(Double::doubleValue).toArray());

		double similarity_score = vector1.cosine(vector2);

		double simple_score_weight = 0.75;
		double similarity_score_weight = 0.25;

		double final_score = simple_score * simple_score_weight + similarity_score * similarity_score_weight;


		System.out.println("answer correctness score is: " +  final_score);




//		if (yesOrNo.equalsIgnoreCase("no")) {
//			SystemMessage notRelatedSystemMessage = new SystemMessage(qaEvaluatorNotRelatedResource);
//			prompt = new Prompt(List.of(userMessage, notRelatedSystemMessage));
//			String reasonForFailure = openAiChatClient.call(prompt).getResult().getOutput().getContent();
//			fail(reasonForFailure);
//		}
//		else {
//			logger.info("Answer is related to question.");
//			assertThat(yesOrNo).isEqualTo("YES");
//		}
	}

	@Test
	public void test() throws JsonProcessingException {
		String question = "What happens when a inpatient is discharged?";
//		String result = questionService.generate(question, false);
		String result = "Upon discharge from an inpatient admission, the individual will be provided with a 30-day supply of medically necessary medication to ensure continuity of treatment and medication adherence. The admitting hospital must establish a stabilization plan for the individual within 48 hours after admission, and a participating Community Connect IMD hospital will not discharge the individual unless the discharge plan ensures they have a place to go and appropriate services will be implemented. Additionally, the community mental health agency or private practitioner will be notified of the date and time of discharge and invited to participate in the discharge planning process.";
		// "I'm not sure, but as far as I know, VMWare doesn't have a famous recipe book. VMWare is a software company known for its virtualization and cloud computing products, not for recipe books."
		evaluateQuestionAndAnswer(question, result, true);
	}

}