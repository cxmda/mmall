package com.mmall.service.impl;import com.mmall.common.Const;import com.mmall.common.ServerResponse;import com.mmall.dao.UserMapper;import com.mmall.pojo.User;import com.mmall.service.IUserService;import com.mmall.util.MD5Util;import com.mmall.util.RedisShardedPoolUtil;import org.apache.commons.lang3.StringUtils;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import java.util.UUID;/** * @author chenqiang * @create 2020-06-28 11:47 */@Servicepublic class UserServiceImpl implements IUserService {    @Autowired    private UserMapper userMapper;    @Override    public ServerResponse<User> login(String username, String password) {        //检查用户名是否存在        int count = userMapper.checkUsername(username);        if (count == 0) {            return ServerResponse.createByErrorMessage("用户不存在");        }        String md5Password = MD5Util.MD5EncodeUtf8(password);        //校验用户名和密码        User user = userMapper.selectLogin(username, md5Password);        if (user == null) {            return ServerResponse.createByErrorMessage("密码错误");        }        user.setPassword(StringUtils.EMPTY);        return ServerResponse.createBySuccess("登录成功", user);    }    @Override    public ServerResponse<String> register(User user) {        //检查用户名是否存在        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);        if (!validResponse.isSuccess()) {            return validResponse;        }        //检查email是否存在        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);        if (!validResponse.isSuccess()) {            return validResponse;        }        user.setRole(Const.Role.ROLE_CUSTOMER);        //密码MD5加密        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));        int resultCount = userMapper.insert(user);        if (resultCount == 0) {            return ServerResponse.createByErrorMessage("注册失败");        }        return ServerResponse.createBySuccessMessage("注册成功");    }    @Override    public ServerResponse<String> checkValid(String str, String type) {        if (StringUtils.isNotBlank(type)) {            if (Const.USERNAME.equals(type)) {                //检查用户名是否存在                int count = userMapper.checkUsername(str);                if (count > 0) {                    return ServerResponse.createByErrorMessage("用户已存在");                }            }            if (Const.EMAIL.equals(type)) {                //检查email是否存在                int i = userMapper.checkEmail(str);                if (i > 0) {                    return ServerResponse.createByErrorMessage("email已存在");                }            }        } else {            return ServerResponse.createByErrorMessage("参数错误");        }        return ServerResponse.createBySuccessMessage("参数校验成功");    }    @Override    public ServerResponse<String> selectQuestion(String username) {        //检查用户名是否存在        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);        if (validResponse.isSuccess()) {            return ServerResponse.createByErrorMessage("用户不存在");        }        String question = userMapper.selectQuestionByUsername(username);        if (StringUtils.isNotBlank(question)) {            return ServerResponse.createBySuccess(question);        }        return ServerResponse.createByErrorMessage("找回密码问题不存在");    }    @Override    public ServerResponse<String> checkAnswer(String username, String question, String answer) {        int resultCount = userMapper.checkAnswer(username, question, answer);        if (resultCount > 0) {            //说明问题及答案是这个用户的，并且是正确的，那么就设置一个token用来修改密码            String forgetToken = UUID.randomUUID().toString();            //TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX + username,forgetToken, 60 * 60 * 12);            return ServerResponse.createBySuccess(forgetToken);        }        return ServerResponse.createByErrorMessage("问题的答案错误");    }    @Override    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {        if (StringUtils.isBlank(forgetToken)) {            return ServerResponse.createByErrorMessage("参数错误，token需要传递");        }        //检查用户名是否存在        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);        if (validResponse.isSuccess()) {            return ServerResponse.createByErrorMessage("用户不存在");        }        //检查token是否正确        //String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);        String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX + username);        if (StringUtils.isBlank(token)) {            return ServerResponse.createByErrorMessage("token无效或过期");        }        if (StringUtils.equals(forgetToken, token)) {            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);            if (rowCount > 0) {                return ServerResponse.createBySuccessMessage("修改密码成功");            }        } else {            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");        }        return ServerResponse.createByErrorMessage("修改密码失败");    }    @Override    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户。因为我们会查询一个count(1)，如果不指定id，那么结果就是true  count>0        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());        if (resultCount == 0) {            return ServerResponse.createByErrorMessage("旧密码错误");        }        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));        int updateCount = userMapper.updateByPrimaryKeySelective(user);        if (updateCount > 0) {            return ServerResponse.createBySuccessMessage("修改密码成功");        }        return ServerResponse.createByErrorMessage("修改密码失败");    }    @Override    public ServerResponse<User> updateInformation(User user) {        //username是不能被更新的        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果是相同的话，不能是我们当前这个用户的        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());        if (resultCount > 0) {            return ServerResponse.createByErrorMessage("email已存在，请更换新的email再重试");        }        User updateUser = new User();        updateUser.setId(user.getId());        updateUser.setEmail(user.getEmail());        updateUser.setQuestion(user.getQuestion());        updateUser.setAnswer(user.getAnswer());        updateUser.setPhone(user.getPhone());        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);        if(updateCount > 0) {            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);        }        return ServerResponse.createByErrorMessage("更新个人信息失败");    }    @Override    public ServerResponse<User> getInformation(Integer userId) {        User user = userMapper.selectByPrimaryKey(userId);        if(user == null){            return ServerResponse.createByErrorMessage("用户不存在");        }        user.setPassword(StringUtils.EMPTY);        return ServerResponse.createBySuccess(user);    }    /**     * 校验是否是管理员     * @param user     * @return     */    @Override    public ServerResponse<String> checkAdminRole(User user){        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){            return ServerResponse.createBySuccess();        }        return ServerResponse.createByError();    }}