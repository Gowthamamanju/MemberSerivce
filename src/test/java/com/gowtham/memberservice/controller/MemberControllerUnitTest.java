package com.gowtham.memberservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.gowtham.memberservice.entity.Member;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerUnitTest {

	public static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	public static List<Member> retrieveMemberList(String json) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, Member.class);
		List<Member> resultMember = objectMapper.readValue(json, typeReference);
		assertTrue(resultMember.size() > 0);
		return resultMember;
	}

	public static Member retrieveMember(String json) throws IOException {
		Member resultMember = objectMapper.readValue(json, Member.class);
		return resultMember;
	}

	public static Date currentdate() throws ParseException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(formatter.format(new Date()));
	}

	private String encodeFileToBase64Binary() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("test.png").getFile());
		try (FileInputStream fileInputStreamReader = new FileInputStream(file)) {
			byte[] bytes = new byte[(int) file.length()];
			fileInputStreamReader.read(bytes);
			return Base64.encodeBase64(bytes).toString();
		}
	}

	private void decodeAsFileFromBase64Binary(String base64Data) throws IOException {
		byte[] decodedImg = Base64.decodeBase64(base64Data);
		Path destinationFile = Paths.get("/tmp", "member.png");
		Files.write(destinationFile, decodedImg);
		File file = destinationFile.toFile();
		assertTrue(file.exists());
	}

	@Test
	public void retrieveAllMembersTestcase() throws Exception {
		this.mockMvc.perform(get("/members")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					List<Member> memberDetails = retrieveMemberList(json);
					assertEquals("gowtham", memberDetails.get(0).getFirstName());
					assertEquals("ss", memberDetails.get(0).getLastName());
				});
	}

	@Test
	public void retrieveMemberByIdTestcase() throws Exception {
		this.mockMvc.perform(get("/member/id/1000")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					Member memberDetail = retrieveMember(json);
					assertEquals("gowtham", memberDetail.getFirstName());
					assertEquals("ss", memberDetail.getLastName());

				});
	}

	@Test
	public void retrieveMemberByFirstNameTestcase() throws Exception {
		this.mockMvc.perform(get("/member/firstname/gowtham")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					Member memberDetail = retrieveMember(json);
					assertEquals(1000l, memberDetail.getId());
					assertEquals("ss", memberDetail.getLastName());
				});
	}

	@Test
	public void completeCrudOperationTestcase() throws Exception {

		String base64EncodedFile = encodeFileToBase64Binary();

		assertNotNull(base64EncodedFile);

		// add new member
		Member newMember = new Member("NewUser", "lastname", currentdate(), 12345, base64EncodedFile);
		this.mockMvc.perform(post("/members").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newMember))).andExpect(status().isCreated());

		// verifies new member added
		this.mockMvc.perform(get("/member/firstname/NewUser")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					Member memberDetail = retrieveMember(json);
					assertEquals(1001l, memberDetail.getId());
					assertEquals("lastname", memberDetail.getLastName());
					decodeAsFileFromBase64Binary(memberDetail.getPicture());
				});

		// updates existing member
		Member updateMember = new Member("UpdateUser", "Updatelastname", currentdate(), 123456, base64EncodedFile);
		this.mockMvc.perform(put("/members/id/1001").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateMember))).andExpect(status().isNoContent());

		// verifies new member updated
		this.mockMvc.perform(get("/member/firstname/UpdateUser")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					Member memberDetail = retrieveMember(json);
					assertEquals(1001l, memberDetail.getId());
					assertEquals("Updatelastname", memberDetail.getLastName());
				});

		// deletes the member
		this.mockMvc.perform(delete("/member/id/1001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void retrieveMemberByIdNotFoundTestcase() throws Exception {
		this.mockMvc.perform(get("/member/id/1")).andDo(print()).andExpect(status().isNotFound());

	}

	@Test
	public void retrieveMemberByFirstNameNotFoundTestcase() throws Exception {
		this.mockMvc.perform(get("/member/firtsname/test")).andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	public void deleteMemberIdNotPresentTestcase() throws Exception {
		this.mockMvc.perform(get("/member/id/1100")).andDo(print()).andExpect(status().isNotFound());
	}

}