package com.spring.board.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.spring.board.common.PagingUtil;
import com.spring.board.common.ResultUtil;
import com.spring.board.dao.BoardDao;
import com.spring.board.dto.BoardDto;
import com.spring.board.dto.CommonDto;
import com.spring.board.form.BoardFileForm;
import com.spring.board.form.BoardForm;
import com.spring.board.form.CommonForm;

@Service
public class BoardService {

	@Autowired
	private BoardDao boardDao;

	/** 게시판 - 목록 조회 */
	public ResultUtil getBoardList(BoardForm boardForm) throws Exception {

		ResultUtil resultUtil = new ResultUtil();

		CommonDto commonDto = new CommonDto();

		int totalCount = boardDao.getBoardCnt(boardForm);
		if (totalCount != 0) {
			CommonForm commonForm = new CommonForm();
			commonForm.setFunction_name(boardForm.getFunction_name());
			commonForm.setCurrent_page_no(boardForm.getCurrent_page_no());
			commonForm.setCount_per_page(10);
			commonForm.setCount_per_list(10);
			commonForm.setTatal_list_count(totalCount);
			commonDto = PagingUtil.setPageUtil(commonForm);
		}

		boardForm.setLimit(commonDto.getLimit());
		boardForm.setOffset(commonDto.getOffset());

		List<BoardDto> list = boardDao.getBoardList(boardForm);

		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("list", list);
		resultMap.put("totalCount", totalCount);
		resultMap.put("pagination", commonDto.getPagination());

		resultUtil.setData(resultMap);
		resultUtil.setState("SUCCESS");

		return resultUtil;
	}

	/** 게시판 - 목록 조회 */
	/*
	 * public List<BoardDto> getBoardList(BoardForm boardForm) throws Exception {
	 * 
	 * return boardDao.getBoardList(boardForm); }
	 */

	/** 게시판 - 상세 조회 */
	public BoardDto getBoardDetail(BoardForm boardForm) throws Exception {

		BoardDto boardDto = new BoardDto();

		String searchType = boardForm.getSearch_type();

		if ("S".equals(searchType)) {

			int updateCnt = boardDao.updateBoardHits(boardForm);

			if (updateCnt > 0) {
				boardDto = boardDao.getBoardDetail(boardForm);
			}

		} else {

			boardDto = boardDao.getBoardDetail(boardForm);
		}

		return boardDto;
	}

	/** 게시판 - 등록 */
	public BoardDto insertBoard(HttpServletRequest request, HttpServletResponse response, BoardForm boardForm) throws Exception {

		BoardDto boardDto = new BoardDto();

		int insertCnt = 0;

		insertCnt = boardDao.insertBoard(boardForm);

		List<BoardFileForm> list = getBoardFileInfo(request, boardForm);

		for (int i = 0; i < list.size(); i++) {
			boardDao.insertBoardFile(list.get(i));
		}

		if (insertCnt > 0) {
			boardDto.setResult("SUCCESS");
		} else {
			boardDto.setResult("FAIL");
		}

		return boardDto;
	}

	/** 게시판 - 첨부파일 정보 조회 */
	public List<BoardFileForm> getBoardFileInfo(HttpServletRequest request, BoardForm boardForm) throws Exception {

		String filePath = "C:\\board\\file";

		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) (HttpServletRequest) request;
		Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

		MultipartFile multipartFile = null;
		String fileName = null;
		String fileExt = null;
		String fileNameKey = null;
		String fileSize = null;

		List<BoardFileForm> list = new ArrayList<BoardFileForm>();
		BoardFileForm boardFileForm = new BoardFileForm();

		int boardSeq = boardForm.getBoard_seq();

		System.out.println("boardSeq : " + boardSeq);

		File file = new File(filePath);

		if (file.exists() == false) {
			file.mkdirs();
		}

		while (iterator.hasNext()) {

			multipartFile = multipartHttpServletRequest.getFile(iterator.next());

			if (multipartFile.isEmpty() == false) {

				fileName = multipartFile.getOriginalFilename();
				fileExt = fileName.substring(fileName.lastIndexOf("."));
				fileNameKey = getRandomString() + fileExt;
				fileSize = String.valueOf(multipartFile.getSize());

				file = new File(filePath + "/" + fileNameKey);

				multipartFile.transferTo(file);

				boardFileForm = new BoardFileForm();
				boardFileForm.setBoard_seq(boardSeq);
				boardFileForm.setFile_name(fileName);
				boardFileForm.setFile_name_key(fileNameKey);
				boardFileForm.setFile_path(filePath);
				boardFileForm.setFile_size(fileSize);
				list.add(boardFileForm);
			}
		}

		return list;
	}

	/** 게시판 - 삭제 */
	public BoardDto deleteBoard(BoardForm boardForm) throws Exception {

		BoardDto boardDto = new BoardDto();

		int deleteCnt = boardDao.deleteBoard(boardForm);

		if (deleteCnt > 0) {
			boardDto.setResult("SUCCESS");
		} else {
			boardDto.setResult("FAIL");
		}

		return boardDto;
	}

	/** 게시판 - 수정 */
	public BoardDto updateBoard(BoardForm boardForm) throws Exception {

		BoardDto boardDto = new BoardDto();

		int deleteCnt = boardDao.updateBoard(boardForm);

		if (deleteCnt > 0) {
			boardDto.setResult("SUCCESS");
		} else {
			boardDto.setResult("FAIL");
		}

		return boardDto;
	}

	/** 게시판 - 답글 등록 */
	public BoardDto insertBoardReply(BoardForm boardForm) throws Exception {

		BoardDto boardDto = new BoardDto();

		BoardDto boardReplayInfo = boardDao.getBoardReplyInfo(boardForm);

		boardForm.setBoard_seq(boardReplayInfo.getBoard_seq());
		boardForm.setBoard_re_lev(boardReplayInfo.getBoard_re_lev());
		boardForm.setBoard_re_ref(boardReplayInfo.getBoard_re_ref());
		boardForm.setBoard_re_seq(boardReplayInfo.getBoard_re_seq());

		int insertCnt = 0;

		insertCnt += boardDao.updateBoardReSeq(boardForm);

		insertCnt += boardDao.insertBoardReply(boardForm);

		if (insertCnt > 0) {
			boardDto.setResult("SUCCESS");
		} else {
			boardDto.setResult("FAIL");
		}

		return boardDto;
	}

	/** 32글자의 랜덤한 문자열(숫자포함) 생성 */
	public static String getRandomString() {

		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
