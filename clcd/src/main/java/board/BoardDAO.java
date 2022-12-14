package board;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class BoardDAO implements BoardService {

	@Autowired @Qualifier("hanul") private SqlSession sql;
	
	@Override
	public int board_insert(BoardVO vo) {
		// TODO Auto-generated method stub
		return sql.insert("board.mapper.insert", vo);
	}

	@Override
	public BoardPage board_list(BoardPage page) {
		// TODO Auto-generated method stub
		page.setTotalList(sql.selectOne("board.mapper.totalList",page));
		page.setList(sql.selectList("board.mapper.list", page));
		return page;
	}

	@Override
	public BoardVO board_detail(int id) {
		// TODO Auto-generated method stub
		return sql.selectOne("board.mapper.detail",id);
	}

	@Override
	public int board_read(int id) {
		// TODO Auto-generated method stub
		return sql.update("board.mapper.read", id);
	}

	@Override
	public int board_update(BoardVO vo) {
		// TODO Auto-generated method stub
		return sql.update("board.mapper.update", vo);
	}

	@Override
	public int board_delete(int id) {
		// TODO Auto-generated method stub
		return sql.delete("board.mapper.delete", id);
	}

	@Override
	public int board_comment_insert(BoardCommentVO vo) {
		// TODO Auto-generated method stub
		return sql.insert("board.mapper.comment_insert",vo);
	}

	@Override
	public int board_comment_update(BoardCommentVO vo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int board_comment_delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<BoardCommentVO> board_comment_list(int pid) {
		// TODO Auto-generated method stub
		return sql.selectList("board.mapper.comment_list",pid);
	}

}
