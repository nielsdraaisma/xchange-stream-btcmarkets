package info.bitrich.xchangestream.btcmarkets.service;

import org.knowm.xchange.utils.Assert;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class BTCMarketsStreamingDigest {

  private final Charset utf8 = Charset.forName("UTF-8");
  private final Mac mac;

  public BTCMarketsStreamingDigest(String algorithm, String secretKey) {
    Assert.notNull(algorithm, "Null algorithm");
    Assert.notNull(secretKey, "Null secretKey");
    try {
      mac = Mac.getInstance(algorithm);
      mac.init(new SecretKeySpec(secretKey.getBytes(utf8), mac.getAlgorithm()));
    } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  public String base64Digest(String message) {
    byte[] bytes = mac.doFinal(message.getBytes(utf8));
    return Base64.getEncoder().encodeToString(bytes);
  }
}
