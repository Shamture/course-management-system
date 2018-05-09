package team.high5.domain.user;

import com.sun.org.apache.xpath.internal.operations.Equals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author : Charles Ma
 * @Date : 2018/5/9 0009
 * @Time : 14:49
 * @Description : User
 */
@MappedSuperclass
public abstract class User extends Equals {
    @Transient
    protected static final Logger logger = LoggerFactory.getLogger(User.class);
    @Id
    @Column(name = "userId")
    private String userId;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;

    /**
     * Encodes a string to MD5
     *
     * @param str String to encode
     * @return Encoded String
     */
    private static String toMD5(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encrypt cannot be null or empty.");
        }
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (byte aHash : hash) {
                if ((0xff & aHash) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & aHash)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & aHash));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("This string cannot be encrypted to md5", str);
        }
        return hexString.toString();
    }

    private boolean isIllegal(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (isIllegal(password)) {
            throw new IllegalArgumentException("Illegal chars in password");
        }

        if (password.equals(this.password)) {
            throw new IllegalArgumentException("Reusing passwords");
        }
        this.password = toMD5(password);
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = super.equals(obj);
        if (!res) {
            return false;
        }
        User other = (User) obj;
        return this.name.equals(other.name);
    }
}
