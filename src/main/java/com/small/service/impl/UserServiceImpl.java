package com.small.service.impl;

import com.small.common.SystemCode;
import com.small.common.SystemConst;
import com.small.common.SystemResponse;
import com.small.common.TonkenCache;
import com.small.dao.UserMapper;
import com.small.pojo.User;
import com.small.service.IUserService;
import com.small.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户ser类
 * Created by 85073 on 2018/5/5.
 */
@Service
public class UserServiceImpl implements IUserService  {

    @Autowired
    private UserMapper userMapper;

    @Override
    public SystemResponse<User> doLogin(String userName, String password) {
        int resultCount = userMapper.checkUserNameIsExist(userName);

        if(resultCount == 0) {
            return SystemResponse.createErrorByMsg(SystemConst.USERNAME_NOT_EXIST);
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLoginUser(userName,md5Password);
        if(null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.PASSWORD_ERROR);
        }
        //置空返回给前端的密码
        user.setPassword(StringUtils.EMPTY);
        return SystemResponse.createSuccessByData(user);
    }

    @Override
    public SystemResponse<String> register(User user) {
        if(null == user) {
            return SystemResponse.createErrorByMsg(SystemConst.SUBMIT_USERINFO_EMPTY);
        }
        //校验用户名
        SystemResponse<String> userNameSystemResponse =  checkValid(user.getUsername(),SystemConst.CHECK_TYPE_USERNAME);
        if(!userNameSystemResponse.isSuccess()) {
            return userNameSystemResponse;
        }

        //校验邮箱
        SystemResponse<String> emailSystemResponse = checkValid(user.getEmail(),SystemConst.CHECK_TYPE_EMAIL);
        if(!emailSystemResponse.isSuccess()) {
            return emailSystemResponse;
        }
        //注册
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setRole(SystemConst.Role.CUSTOMER);
        int resultCount = userMapper.insert(user);
        if(resultCount == 0) {
            return SystemResponse.createErrorByMsg(SystemConst.REGISTER_FAIL);
        }
        return SystemResponse.createSuccessByMsg(SystemConst.REGISTER_SUCCESS);
    }

    @Override
    public SystemResponse<String> checkValid(String str, String type) {
        if(StringUtils.isBlank(type) || StringUtils.isBlank(str)) {
            return SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        //校验用户名
        if(SystemConst.CHECK_TYPE_USERNAME.equals(type)) {
            int resultCount = userMapper.checkUserNameIsExist(str);
            if(resultCount>0) {
                return SystemResponse.createErrorByMsg(SystemConst.USERNAME_IS_REGISTER);
            }
        }else if(SystemConst.CHECK_TYPE_EMAIL.equals(type)) {
            int resultCount = userMapper.checkEmailIsExist(str);
            if(resultCount>0) {
                return SystemResponse.createErrorByMsg(SystemConst.EMAIL_IS_REGISTER);
            }
        }else {
            return SystemResponse.createErrorByMsg(SystemConst.UNSUPPORT_CHECK_TYPE);
        }

        return SystemResponse.createSuccessByMsg(SystemConst.CHECK_PASS);
    }


    public SystemResponse<String> selectQuestion(String username) {
        //校验用户是否存在
        SystemResponse<String> response = checkValid(username,SystemConst.CHECK_TYPE_USERNAME);
        if(response.isSuccess()) {//用户不存在
            return SystemResponse.createErrorByMsg(SystemConst.USERNAME_NOT_EXIST);
        }
        String question = userMapper.selectQuestion(username);
        if(StringUtils.isBlank(question)) {
            return SystemResponse.createErrorByMsg(SystemConst.QUESTION_IS_EMPTY);
        }
        return SystemResponse.createSuccessByData(question);
    }

    @Override
    public SystemResponse<String> checkAnswer(String username, String question, String answer) {
        //校验用户是否存在
        SystemResponse response = checkValid(username,SystemConst.CHECK_TYPE_USERNAME);
        if(response.isSuccess()) {
            return SystemResponse.createErrorByMsg(SystemConst.USERNAME_NOT_EXIST);
        }
        //判断答案的正确性
        int selectResult = userMapper.checkAnswer(username,question,answer);
        if(selectResult==0) {
            return  SystemResponse.createErrorByMsg(SystemConst.ANSWER_IS_ERROR);
        }

        //生成forgetToken 用户控制用户横向越权
        String forgetToken = UUID.randomUUID().toString();
        TonkenCache.setKey(SystemConst.TOKEN_PRIFIX+username,forgetToken);
        return SystemResponse.createSuccessByData(forgetToken);
    }

    @Override
    public SystemResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {

        if(StringUtils.isBlank(username) || StringUtils.isBlank(newPassword) || StringUtils.isBlank(forgetToken)){
            SystemResponse.createErrorByMsg(SystemConst.ARGS_ERROR);
        }
        //校验用户
        SystemResponse response = checkValid(username,SystemConst.CHECK_TYPE_USERNAME);
        if(response.isSuccess()) {
            return SystemResponse.createErrorByMsg(SystemConst.USERNAME_NOT_EXIST);
        }
        //校验forgetToken
        String servForgetToken = TonkenCache.getKey(SystemConst.TOKEN_PRIFIX+username);
        if(StringUtils.isBlank(servForgetToken)) {
            return SystemResponse.createErrorByMsg(SystemConst.LOCAL_TOKEN_REPAIR);
        }
        if(StringUtils.equals(servForgetToken,forgetToken)) {
            String md5NewPassword = MD5Util.MD5EncodeUtf8(newPassword);
            int updateCount = userMapper.updateNewPassword(username,md5NewPassword);
            if(updateCount >0) {
                return  SystemResponse.createSuccessByMsg(SystemConst.UPDATE_SUCCESS);
            }
        } else {
            return SystemResponse.createErrorByMsg(SystemConst.TOKEN_ERROR);
        }
        return SystemResponse.createErrorByMsg(SystemConst.UPDATE_ERROR);
    }

    @Override
    public SystemResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        //检查密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount == 0) {
            return SystemResponse.createErrorByMsg(SystemConst.OLD_PASSWORD_ERROR);
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount == 0) {
            return SystemResponse.createErrorByMsg(SystemConst.UPDATE_ERROR);
        }

        return SystemResponse.createSuccessByMsg(SystemConst.UPDATE_SUCCESS);
    }

    @Override
    public SystemResponse<User> updateUserInfo(User user) {
        //检查更新的email是否存在
        int resultCount = userMapper.checkEmilById(user.getEmail(),user.getId());
        if(resultCount>0) {
            return SystemResponse.createErrorByMsg(SystemConst.EMAIL_IS_REGISTER);
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0) {
            return SystemResponse.createSuccessByMsg(SystemConst.UPDATE_USERINFO_SUCCESS);
        }
        return SystemResponse.createErrorByMsg(SystemConst.UPDATE_USERINFO_ERROR);
    }

    @Override
    public SystemResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(null == user) {
            return  SystemResponse.createErrorByMsg(SystemConst.USERNAME_NOT_EXIST);
        }
        user.setPassword(StringUtils.EMPTY);
        return SystemResponse.createSuccessByData(user);
    }


    /**
     * 检查用户是否是管理员
     * @param user user
     * @return boolean
     */
    public boolean checkAmdinRole(User user) {
        if(user == null) {
            return false;
        }
        if(user.getRole()==SystemConst.Role.AMDIN) {
            return true;
        }else {
            return false;
        }
    }
}
