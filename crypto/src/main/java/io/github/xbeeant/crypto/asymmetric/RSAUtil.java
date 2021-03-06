package io.github.xbeeant.crypto.asymmetric;

import io.github.xbeeant.crypto.Base64Util;
import io.github.xbeeant.crypto.KeyString;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;

/**
 * 该算法于1977年由美国麻省理工学院MIT(Massachusetts Institute of Technology)的Ronal Rivest，Adi Shamir和Len Adleman三位年轻教授提出，并以三人的姓氏Rivest，Shamir和Adlernan命名为RSA算法，是一个支持变长密钥的公共密钥算法，需要加密的文件快的长度也是可变的!
 * 所谓RSA加密算法，是世界上第一个非对称加密算法，也是数论的第一个实际应用。它的算法如下：
 * 1.找两个非常大的质数p和q（通常p和q都有155十进制位或都有512十进制位）并计算n=pq，k=(p-1)(q-1)。
 * 2.将明文编码成整数M，保证M不小于0但是小于n。
 * 3.任取一个整数e，保证e和k互质，而且e不小于0但是小于k。加密钥匙（称作公钥）是(e, n)。
 * 4.找到一个整数d，使得ed除以k的余数是1（只要e和n满足上面条件，d肯定存在）。解密钥匙（称作密钥）是(d, n)。
 * 加密过程： 加密后的编码C等于M的e次方除以n所得的余数。
 * 解密过程： 解密后的编码N等于C的d次方除以n所得的余数。
 * 只要e、d和n满足上面给定的条件。M等于N。
 *
 * @author xiaobiao
 * @version 2020/10/7
 */
public class RSAUtil {
    public static final String RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    public static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final Provider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();

    private RSAUtil() {
    }

    /**
     * 建立密钥对
     *
     * @param keySize 关键尺寸
     * @return {@link KeyPair}
     * @throws NoSuchAlgorithmException 没有这样的算法异常
     */
    public static KeyPair buildKeyPair(int keySize) throws NoSuchAlgorithmException {
        /* RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        return buildKeyPair(keySize, sr);
    }

    /**
     * 建立密钥对
     *
     * @param keySize 关键尺寸
     * @param sr      可信任的随机数源
     * @return {@link KeyPair}
     * @throws NoSuchAlgorithmException 没有这样的算法异常
     */
    public static KeyPair buildKeyPair(int keySize, SecureRandom sr) throws NoSuchAlgorithmException {
        /* 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(AsymmetricAlgorithm.RSA.getValue());
        /* 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(keySize, sr);
        /* 生成密匙对 */
        return kpg.genKeyPair();
    }

    /**
     * 解密
     *
     * @param privateKey   私钥
     * @param cryptography 密文
     * @return {@link byte[]}
     * @throws NoSuchAlgorithmException           没有这样的算法异常
     * @throws InvalidKeyException                无效的关键例外
     * @throws BadPaddingException                坏填充例外
     * @throws IllegalBlockSizeException          非法的块大小异常
     * @throws InvalidKeySpecException            无效的关键规范异常
     * @throws NoSuchPaddingException             没有这样的填充例外
     * @throws IOException                        IO异常
     * @throws InvalidAlgorithmParameterException 算法参数异常
     */
    public static byte[] decrypt(String privateKey, String cryptography) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, IOException, InvalidAlgorithmParameterException {
        Key key = toPrtoivateKey(privateKey);
        /* 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM, BOUNCY_CASTLE_PROVIDER);

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] b1 = Base64Util.decode(cryptography.getBytes());

        KeyFactory keyFactory = KeyFactory.getInstance(AsymmetricAlgorithm.RSA.getValue());

        int inputLen = b1.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        RSAPrivateKeySpec keySpec = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class);
        // assumes the bitLength is a multiple of 8 (check first!)
        int keySize = keySpec.getModulus().toString(2).length();
        int maxDecryptBlock = keySize / 8;
        byte[] cache;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxDecryptBlock) {
                cache = cipher.doFinal(b1, offSet, maxDecryptBlock);
            } else {
                cache = cipher.doFinal(b1, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxDecryptBlock;
        }

        byte[] b = out.toByteArray();
        out.close();

        return b;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @return {@link PrivateKey}
     * @throws NoSuchAlgorithmException 没有这样的算法异常
     * @throws InvalidKeySpecException  无效的关键规范异常
     */
    public static PrivateKey toPrtoivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64Util.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(AsymmetricAlgorithm.RSA.getValue());
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 解密
     *
     * @param privateKey 私钥
     * @param encrypted  加密
     * @param keySize    长度
     * @return {@link byte[]}
     * @throws NoSuchAlgorithmException  没有这样的算法异常
     * @throws InvalidKeyException       无效的关键例外
     * @throws BadPaddingException       坏填充例外
     * @throws IllegalBlockSizeException 非法的块大小异常
     * @throws NoSuchPaddingException    没有这样的填充例外
     * @throws IOException               IO异常
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted, int keySize) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        // 解密数据，分段解密
        Cipher dec = Cipher.getInstance(RSA_ALGORITHM, BOUNCY_CASTLE_PROVIDER);
        dec.init(Cipher.DECRYPT_MODE, privateKey);

        ByteArrayOutputStream ptStream = new ByteArrayOutputStream();
        int off = 0;
        while (off < encrypted.length) {
            int toCrypt = Math.min(keySize, encrypted.length - off);
            byte[] partialPt = dec.doFinal(encrypted, off, toCrypt);
            ptStream.write(partialPt);
            off += toCrypt;
        }

        return ptStream.toByteArray();
    }

    public static byte[] encrypt(String publicKey, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        PublicKey key = toPublicKey(publicKey);
        return encrypt(key, message);
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @return {@link PublicKey}
     * @throws NoSuchAlgorithmException 没有这样的算法异常
     * @throws InvalidKeySpecException  无效的关键规范异常
     */
    public static PublicKey toPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Util.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(AsymmetricAlgorithm.RSA.getValue());
        return keyFactory.generatePublic(keySpec);
    }

    public static byte[] encrypt(PublicKey publickKey, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM, BOUNCY_CASTLE_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, publickKey);

        return cipher.doFinal(message.getBytes(UTF8));
    }

    /**
     * 加载私钥
     *
     * @param privateKeyStr 私钥str
     * @return {@link RSAPrivateKey}
     * @throws NoSuchAlgorithmException 没有这样的算法异常
     * @throws InvalidKeySpecException  无效的关键规范异常
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64Util.base64StringDecode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥str
     * @return {@link RSAPublicKey}
     * @throws NoSuchAlgorithmException 没有这样的算法异常
     * @throws InvalidKeySpecException  无效的关键规范异常
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64Util.base64StringDecode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static KeyString toKeyString(KeyPair keyPair) {
        /* 得到公钥 */
        Key publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String pub = Base64Util.encodeToString(publicKeyBytes);
        /* 得到私钥 */
        Key privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = Base64Util.encodeToString(privateKeyBytes);

        RSAPublicKey rsp = (RSAPublicKey) keyPair.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        byte[] deBase64Value = Base64.getEncoder().encode(b);
        String retValue = new String(deBase64Value);

        return new KeyString(pub, pri, retValue);
    }
}
