package com.small.dao;

import com.small.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserNameIsExist(String userName);

    int checkEmailIsExist(String email);

    User selectLoginUser(@Param("userName") String userName, @Param("password") String password);

    String selectQuestion(String username);

    int checkAnswer(@Param("username")String username,@Param("question") String question,@Param("answer")String answer);

    int updateNewPassword(@Param("username")String username,@Param("password") String password);

    int checkPassword(@Param("password")String password,@Param("id")Integer id);

    int checkEmilById(@Param("email") String email,@Param("id") Integer id);
}