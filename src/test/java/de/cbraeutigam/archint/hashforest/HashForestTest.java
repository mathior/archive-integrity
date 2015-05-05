package de.cbraeutigam.archint.hashforest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Christof Bräutigam (christof.braeutigam@cbraeutigam.de)
 * @version 2015-05-05T15:03:28
 * @since 2014-12-12
 *
 */
public class HashForestTest {

	private static String[] hashStrings = new String[] {
			"c6a83d85727d96e654b5849b872e776633ea689cff7c069060edcdc620ce542458d5d308e7f4e68ed2cfcbfd69181b8158a92d7cb51aeeb217250fb8c430bbf5",
			"f609226a348d18736f869c71b80ce70ad29244f9b08e83f06345015e0af6c96e3f6c92ef4ea72bc47e22292b8eb4e3e049ca6b164b18c967fa43fc65b9f16a7f",
			"1449c17ba65119c245be83488974df186eb0dc0b06749037d76d96b7fb154ee4634b8446c1cef203d5f39fac4dd5f05327f6cf978ff97b968439bd9a4d7ac579",
			"5faa0d87facca82f687b4ca2665ec79eeb5ef6b0a14d8c9c8be4f29f121b1a2f5941011720290b5662723f2357e712453399c10065304d85a77e913b3c680d28",
			"7f519f4a004d9a8f1cc22e2efb0f0c41148cbcd64dac3b9b935669d11e42d06216cae3ffcb26b9d7dc31ed15ccf2dca7d07bbf1ba497563a94d02b85eb9c1001",
			"c87c63340759c7df01ad260e5613bfedc78381898ad0efc988300ce75ec11522f5947ace5537da36a3ac22fb7e43ff12259577e59bf1285963d21b48685a6ddc",
			"a8a844bbcea0bc23bfe79a2891352f22562aee04e41f2936c6e437b520f9c32ecf7637bf14dbf73e8b92e734ae04482797f3438e7cf0e1942f118d90c7231e6a",
			"b3b273f6d5ce6d7d9ce8c161f59d9889f89c456a9af3f35a9fa109a7a0aa283701c147f7e111e86bbe304c9890ed318ff841881112ddad796dd0a025c7596336",
			"2a7d1242a84d2554c9403e9c090fedad9e0a82568a7c229fe1cb10679e59322873791a99d7eb6627e479187aace1a23040f50731e53cbfdb4b5f4343a58b9e36",
			"9dab791e057c4c2d84596d72afb1d340812018ccf0a930b91723a3c3aa1f17a8d10d1b73fd761b6cb76b37ba79d590dc3158740e680f69ac8890228e37c6d614",
			"1b69d66a63c34630730b43e8a2ecb6aad6f3a993fc0ca28ff428d522e55c91fc4245a6208fdbd3501a6302da862d76e13171b679983084afe38187f137963b6e",
			"08b6c989061c0aab2fdf6abed38bd7b2c398a7b1e7703ba89ca75967b77ac8f9a145472a4634d34431e77f27ed7757bc7f69d7af6e2ff264c51ca1367dbbfcfd",
			"4006b6ffb538a0caeea77b84ddb71ee0f618b03d381e3704e199f6b489f2e574873af8002607e7cb8959f1233a4032dd400208e10dadcd3ececb138cfd7b8069",
			"e91ec25ebbae1be16bce6aebddc16553f6aa384eaa1a98bc9379918ddbb2ab122976b1a47976351e86272f8c82ce20b9ecdada38cda7b40635b9b90de1e66903",
			"283827d12876af7da450b13918bba23942f0d8f70f8b92ccff1e1cde1657a6cf86783539b2dac4d47b817fba6cc7a13ba511f1049e0c10ebf19f1e56f586444b",
			"768dd953bc232efcfad84cbde681e9ca8e275a9b1962aa249f9d0ba0ce8b831f675d7423421aa31706bcf221b27badae79811051660203bdf34dcbb1e40b2b18",
			"283827d12876af7da450b13918bba23942f0d8f70f8b92ccff1e1cde1657a6cf86783539b2dac4d47b817fba6cc7a13ba511f1049e0c10ebf19f1e56f586444b",
			"47e86c0fbdd2808995ccc7b253aca2dee323f475376776ad53d60d5d5b9ab6e315b52a00df57e56a12308a0ec438ef366daa77cc44098e16681910faf271d7ad",
			"5925a980f76a1e9707efaae28bf9baabea7bf8e6d190a11f591246f55165d69cd7f5fadf7316b331c0424a9dcaae82f1119a2d278b7e8506f4bdfd7beee80340",
			"226d9d0b8a8b8ce4b2a6e42d886a244fa8aa08c8605df441a6bc441f7520809119baa96c286fcaf6122aef78db8e492048ec68191a493d189e9a142c302e446f",
			"45e3a229e30d3382ed2bbedde810164c94487cbc6aafa969b920dad03625c2ce9b191dd37b86ed44d6bafaed9a04e2d67a562df93e7268cec308223791423883",
			"b835bdb46e7ad5fe5f3116adc9f37acb26d81cdd3d7df11ff3824ea44594980a754fdf5f121f3b1bb73e1994e823c97d60b721361e83e1e0db9193a8af21b275",
			"0c27f942e4900da1dd6b0ef1e30fa34e713b65672aad1989d526dbd62feb94ef24ca90e164420d68fc0f75c6ac130befe7205eb06830ad04504ed38211f4383a" };

	private List<SHA512HashValue> sha512Hashes = new ArrayList<SHA512HashValue>();

	private SHA512HashValue[] tree1 = new SHA512HashValue[1];
	private SHA512HashValue[] tree2 = new SHA512HashValue[3];
	private SHA512HashValue[] tree3 = new SHA512HashValue[1];
	private SHA512HashValue[] tree4 = new SHA512HashValue[7];
	private SHA512HashValue[] tree5 = new SHA512HashValue[1];

	private List<SHA512HashValue[]> forest = new ArrayList<SHA512HashValue[]>();

	@Before
	public void setup() {
		try {
			for (String s : hashStrings) {
				sha512Hashes.add(new SHA512HashValue(s));
			}

			tree1[0] = new SHA512HashValue(hashStrings[0]);

			tree2[0] = new SHA512HashValue(
					"45c05282721ad94ad68f33b8dfe4cb67b4f75bffa7b3e62144ab3e8230170b96836e99362ecd51a8f7b8b4d7f3f196a3d11a072bba6e8d3e1d92ad225fc5ab6e");
			tree2[1] = new SHA512HashValue(hashStrings[0]);
			tree2[2] = new SHA512HashValue(hashStrings[1]);

			tree3[0] = new SHA512HashValue(hashStrings[2]);

			tree4[0] = new SHA512HashValue(
					"819a98b0c3fdd686a79fd4fd52fa306bdabc7e0db7017b8cd0ed1150713d77571118c90136a0b289f49647c1c09aefae3a45b6b6481ec85b0fdc73108148297f");
			tree4[1] = new SHA512HashValue(
					"45c05282721ad94ad68f33b8dfe4cb67b4f75bffa7b3e62144ab3e8230170b96836e99362ecd51a8f7b8b4d7f3f196a3d11a072bba6e8d3e1d92ad225fc5ab6e");
			tree4[2] = new SHA512HashValue(
					"2ee669e9f6fbe04d3f8da49d5031f954c01f2889da315b2e3e0efef1e3ac5efb83300a23178677dd9998bb0abe5e4545f803fa6beda4a65f73611df18e737b1c");
			tree4[3] = new SHA512HashValue(hashStrings[0]);
			tree4[4] = new SHA512HashValue(hashStrings[1]);
			tree4[5] = new SHA512HashValue(hashStrings[2]);
			tree4[6] = new SHA512HashValue(hashStrings[3]);

			tree5[0] = new SHA512HashValue(hashStrings[4]);

			SHA512HashValue[] tf0 = new SHA512HashValue[] {
					new SHA512HashValue(
							"2c060c3dc1ac0a9e3cb53137e9d273da158232c0ab6a5ec953e7f1d81844b3b47d9acce5b9795fd531751649e14079f8e88b81512b73ad92892939d3f7457850"),
					new SHA512HashValue(
							"5d94cc19c09711fc7e30b34e29dbac352c37ebfc1a259673a11a7b63c15992362975cd98106bab8a2bebb175ea21226eff524c7433cbf87abe4e13231cbc45e1"),
					new SHA512HashValue(
							"3aaface2acfca4644ccc1f0bfa8f1b680ea0076ba60ac563c99c80c14185b648ad2a87ae236b78666ef51b861875f93180bd0a487e351955c7e5a8388435d789"),
					new SHA512HashValue(
							"819a98b0c3fdd686a79fd4fd52fa306bdabc7e0db7017b8cd0ed1150713d77571118c90136a0b289f49647c1c09aefae3a45b6b6481ec85b0fdc73108148297f"),
					new SHA512HashValue(
							"9155b98b2f58c867285cdfb241e685b383694f77038fbc8cde63ce5dcbe2036d0f7ad9acdaa58fc657bde5c2df0963cfb56f9b1f6753a14541644c68a414b487"),
					new SHA512HashValue(
							"1d9234588af19c681c401f6f452c98a69d96c1303b348a436c1ac110a9bd6a284f1528d3ed9737a4ac070be4c5ac1ce7331f51b84a0f9fcb0972c759ada70c66"),
					new SHA512HashValue(
							"aa83e7c6a5fa7a4b20a7faf4280853327871544d49c478815b6442b882297eec22a4212a395eb54acd828b53c8b1a407e1136568bf7b7260d0a63cb02e2efbb3"),
					new SHA512HashValue(
							"45c05282721ad94ad68f33b8dfe4cb67b4f75bffa7b3e62144ab3e8230170b96836e99362ecd51a8f7b8b4d7f3f196a3d11a072bba6e8d3e1d92ad225fc5ab6e"),
					new SHA512HashValue(
							"2ee669e9f6fbe04d3f8da49d5031f954c01f2889da315b2e3e0efef1e3ac5efb83300a23178677dd9998bb0abe5e4545f803fa6beda4a65f73611df18e737b1c"),
					new SHA512HashValue(
							"f2a1d385946da18e7a1efa593fbdfa7f518ac317386b93efdf398dac354b8467fe2a50a438a6714ce63823692cc78d1d066610aa8c3cf8ae3b4f191efddb1adf"),
					new SHA512HashValue(
							"bc7736524be2bb367bd0103045579b000daaeb84643a9e63c1541911238d6489e275c9573ba566652b8600670d02e1710f9b29c0f1a9e40b8aecd0397206f007"),
					new SHA512HashValue(
							"ff35d55d9e5bae862bcf0cc5d46746612fad9f2d0dba29e0873cf4ac8ef7edfd44e3429733dfc270d9dedb1f0ce07db5c8d5e7c1ab8d582c0d6486f728d7ea96"),
					new SHA512HashValue(
							"62321ca1d4e6313fea1527ede433d8e2d2bc0b0ea59118d243af697a25c15c60ab77351815fa48d6ceb0b254dc306b25912ad34abb5746aa363503a7fdaab7a6"),
					new SHA512HashValue(
							"dfa6a0396bd65001d165d96672a4723f8ce0bf0b398fa656550d102bc3516cf6fafdf9ea33aaf2076abbfdc282b9c3d4458ba875320fe6eabaa10ca031f36b52"),
					new SHA512HashValue(
							"da982a391b0b5f9c92dee092c61e9ec34268c47060dc95cb7ebe712770497ab85e5277032d656e9c1b2bc1133e3b3fc499a570760d08e244e1ab84c1af298696"),
					new SHA512HashValue(
							"c6a83d85727d96e654b5849b872e776633ea689cff7c069060edcdc620ce542458d5d308e7f4e68ed2cfcbfd69181b8158a92d7cb51aeeb217250fb8c430bbf5"),
					new SHA512HashValue(
							"f609226a348d18736f869c71b80ce70ad29244f9b08e83f06345015e0af6c96e3f6c92ef4ea72bc47e22292b8eb4e3e049ca6b164b18c967fa43fc65b9f16a7f"),
					new SHA512HashValue(
							"1449c17ba65119c245be83488974df186eb0dc0b06749037d76d96b7fb154ee4634b8446c1cef203d5f39fac4dd5f05327f6cf978ff97b968439bd9a4d7ac579"),
					new SHA512HashValue(
							"5faa0d87facca82f687b4ca2665ec79eeb5ef6b0a14d8c9c8be4f29f121b1a2f5941011720290b5662723f2357e712453399c10065304d85a77e913b3c680d28"),
					new SHA512HashValue(
							"7f519f4a004d9a8f1cc22e2efb0f0c41148cbcd64dac3b9b935669d11e42d06216cae3ffcb26b9d7dc31ed15ccf2dca7d07bbf1ba497563a94d02b85eb9c1001"),
					new SHA512HashValue(
							"c87c63340759c7df01ad260e5613bfedc78381898ad0efc988300ce75ec11522f5947ace5537da36a3ac22fb7e43ff12259577e59bf1285963d21b48685a6ddc"),
					new SHA512HashValue(
							"a8a844bbcea0bc23bfe79a2891352f22562aee04e41f2936c6e437b520f9c32ecf7637bf14dbf73e8b92e734ae04482797f3438e7cf0e1942f118d90c7231e6a"),
					new SHA512HashValue(
							"b3b273f6d5ce6d7d9ce8c161f59d9889f89c456a9af3f35a9fa109a7a0aa283701c147f7e111e86bbe304c9890ed318ff841881112ddad796dd0a025c7596336"),
					new SHA512HashValue(
							"2a7d1242a84d2554c9403e9c090fedad9e0a82568a7c229fe1cb10679e59322873791a99d7eb6627e479187aace1a23040f50731e53cbfdb4b5f4343a58b9e36"),
					new SHA512HashValue(
							"9dab791e057c4c2d84596d72afb1d340812018ccf0a930b91723a3c3aa1f17a8d10d1b73fd761b6cb76b37ba79d590dc3158740e680f69ac8890228e37c6d614"),
					new SHA512HashValue(
							"1b69d66a63c34630730b43e8a2ecb6aad6f3a993fc0ca28ff428d522e55c91fc4245a6208fdbd3501a6302da862d76e13171b679983084afe38187f137963b6e"),
					new SHA512HashValue(
							"08b6c989061c0aab2fdf6abed38bd7b2c398a7b1e7703ba89ca75967b77ac8f9a145472a4634d34431e77f27ed7757bc7f69d7af6e2ff264c51ca1367dbbfcfd"),
					new SHA512HashValue(
							"4006b6ffb538a0caeea77b84ddb71ee0f618b03d381e3704e199f6b489f2e574873af8002607e7cb8959f1233a4032dd400208e10dadcd3ececb138cfd7b8069"),
					new SHA512HashValue(
							"e91ec25ebbae1be16bce6aebddc16553f6aa384eaa1a98bc9379918ddbb2ab122976b1a47976351e86272f8c82ce20b9ecdada38cda7b40635b9b90de1e66903"),
					new SHA512HashValue(
							"283827d12876af7da450b13918bba23942f0d8f70f8b92ccff1e1cde1657a6cf86783539b2dac4d47b817fba6cc7a13ba511f1049e0c10ebf19f1e56f586444b"),
					new SHA512HashValue(
							"768dd953bc232efcfad84cbde681e9ca8e275a9b1962aa249f9d0ba0ce8b831f675d7423421aa31706bcf221b27badae79811051660203bdf34dcbb1e40b2b18") };

			SHA512HashValue[] tf1 = new SHA512HashValue[] {
					new SHA512HashValue(
							"8bbea35018afc9340eb899ed90eb55ea6e30ea7877340797a751c90ea1576d8b12aeea64e3674b0760b75a183d499706cfe7c8fee8584a576107de54e9e41815"),
					new SHA512HashValue(
							"4448170d9a7c2ea5b7480b4091aa9be67a0cf6ae08d60975c774b94e2fed1e5d1a188263f63b2de5b600f95c02690773170d1969f942754dd8203c1c8241d7df"),
					new SHA512HashValue(
							"4f58cdb3daa2188a3e26b047f79b44a343e97d0a1dcad6ec1d60f772010c1e829e0f637e57ea5993f8874f845dcaa9b41c721a3903532267c3676b21c96954ca"),
					new SHA512HashValue(
							"283827d12876af7da450b13918bba23942f0d8f70f8b92ccff1e1cde1657a6cf86783539b2dac4d47b817fba6cc7a13ba511f1049e0c10ebf19f1e56f586444b"),
					new SHA512HashValue(
							"47e86c0fbdd2808995ccc7b253aca2dee323f475376776ad53d60d5d5b9ab6e315b52a00df57e56a12308a0ec438ef366daa77cc44098e16681910faf271d7ad"),
					new SHA512HashValue(
							"5925a980f76a1e9707efaae28bf9baabea7bf8e6d190a11f591246f55165d69cd7f5fadf7316b331c0424a9dcaae82f1119a2d278b7e8506f4bdfd7beee80340"),
					new SHA512HashValue(
							"226d9d0b8a8b8ce4b2a6e42d886a244fa8aa08c8605df441a6bc441f7520809119baa96c286fcaf6122aef78db8e492048ec68191a493d189e9a142c302e446f") };

			SHA512HashValue[] tf2 = new SHA512HashValue[] {
					new SHA512HashValue(
							"bc5ddf951d6198200ac817dbfb627b3c36624aeca56cc933881bddafd96cd6022eb394b218a9e37e8724c6839f3e35b6bf0b340f22b926bd06859aaee835b2e8"),
					new SHA512HashValue(
							"45e3a229e30d3382ed2bbedde810164c94487cbc6aafa969b920dad03625c2ce9b191dd37b86ed44d6bafaed9a04e2d67a562df93e7268cec308223791423883"),
					new SHA512HashValue(
							"b835bdb46e7ad5fe5f3116adc9f37acb26d81cdd3d7df11ff3824ea44594980a754fdf5f121f3b1bb73e1994e823c97d60b721361e83e1e0db9193a8af21b275") };

			SHA512HashValue[] tf3 = new SHA512HashValue[] { new SHA512HashValue(
					"0c27f942e4900da1dd6b0ef1e30fa34e713b65672aad1989d526dbd62feb94ef24ca90e164420d68fc0f75c6ac130befe7205eb06830ad04504ed38211f4383a") };

			forest.add(tf0);
			forest.add(tf1);
			forest.add(tf2);
			forest.add(tf3);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testIsEmpty() {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		assertTrue(hf.isEmpty());
		hf.update(sha512Hashes.get(0));
		assertFalse(hf.isEmpty());
	}
	

	@Test
	public void testUpdate() {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		assertTrue(hf.isEmpty());
		
		hf.update(sha512Hashes.get(0));
		assertTrue(hf.getLeafs().size() == 1);
		assertEquals(hf.getLeafs().get(0), sha512Hashes.get(0));
		
		hf.update(sha512Hashes.get(1));
		assertTrue(hf.getLeafs().size() == 2);
		assertEquals(hf.getLeafs().get(1), sha512Hashes.get(1));
		
		hf.update(sha512Hashes.get(2));
		assertTrue(hf.getLeafs().size() == 3);
		assertEquals(hf.getLeafs().get(2), sha512Hashes.get(2));

		HashForest<SHA512HashValue> hfComplete = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete.update(hv);
		}
		assertTrue(hfComplete.getLeafs().size() == 23);
		assertEquals(hfComplete.getLeafs(), sha512Hashes);
	}

	@Test
	public void testGetLeafs() {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		assertTrue(hf.isEmpty());
		hf.update(sha512Hashes.get(0));
		hf.update(sha512Hashes.get(1));
		hf.update(sha512Hashes.get(2));
		assertTrue(hf.getLeafs().size() == 3);
		assertEquals(hf.getLeafs(), sha512Hashes.subList(0, 3));

		HashForest<SHA512HashValue> hfComplete = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete.update(hv);
		}
		assertTrue(hfComplete.getLeafs().size() == 23);
		assertEquals(hfComplete.getLeafs(), sha512Hashes);
	}

	@Test
	public void testGetTrees() {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		assertTrue(hf.getTrees().isEmpty());

		hf.update(sha512Hashes.get(0));
		assertTrue(hf.getTrees().size() == 1);
		assertEquals(hf.getTrees().get(0), tree1);

		hf.update(sha512Hashes.get(1));
		assertTrue(hf.getTrees().size() == 1);
		assertEquals(hf.getTrees().get(0), tree2);

		hf.update(sha512Hashes.get(2));
		assertTrue(hf.getTrees().size() == 2);
		assertEquals(hf.getTrees().get(0), tree2);
		assertEquals(hf.getTrees().get(1), tree3);

		hf.update(sha512Hashes.get(3));
		assertTrue(hf.getTrees().size() == 1);
		assertEquals(hf.getTrees().get(0), tree4);

		hf.update(sha512Hashes.get(4));
		assertTrue(hf.getTrees().size() == 2);
		assertEquals(hf.getTrees().get(0), tree4);
		assertEquals(hf.getTrees().get(1), tree5);

		HashForest<SHA512HashValue> hfComplete = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete.update(hv);
		}
		assertTrue(hfComplete.getTrees().size() == 4);
		assertEquals(hfComplete.getTrees().get(0), forest.get(0));
		assertEquals(hfComplete.getTrees().get(1), forest.get(1));
		assertEquals(hfComplete.getTrees().get(2), forest.get(2));
		assertEquals(hfComplete.getTrees().get(3), forest.get(3));
	}

	@Test
	public void testGetRoots() {
		HashForest<SHA512HashValue> hf = new HashForest<SHA512HashValue>();
		assertTrue(hf.isEmpty());

		hf.update(sha512Hashes.get(0));
		assertTrue(hf.getRoots().size() == 1);
		assertEquals(hf.getRoots().get(0), sha512Hashes.get(0));

		hf.update(sha512Hashes.get(1));
		assertTrue(hf.getRoots().size() == 1);
		assertEquals(hf.getRoots().get(0), tree2[0]);

		hf.update(sha512Hashes.get(2));
		assertTrue(hf.getRoots().size() == 2);
		assertEquals(hf.getRoots().get(0), tree2[0]);
		assertEquals(hf.getRoots().get(1), tree3[0]);

		hf.update(sha512Hashes.get(3));
		assertTrue(hf.getRoots().size() == 1);
		assertEquals(hf.getRoots().get(0), tree4[0]);

		hf.update(sha512Hashes.get(4));
		assertTrue(hf.getRoots().size() == 2);
		assertEquals(hf.getRoots().get(0), tree4[0]);
		assertEquals(hf.getRoots().get(1), tree5[0]);

		HashForest<SHA512HashValue> hfComplete = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete.update(hv);
		}
		assertTrue(hfComplete.getRoots().size() == 4);
		assertEquals(hfComplete.getRoots().get(0), forest.get(0)[0]);
		assertEquals(hfComplete.getRoots().get(1), forest.get(1)[0]);
		assertEquals(hfComplete.getRoots().get(2), forest.get(2)[0]);
		assertEquals(hfComplete.getRoots().get(3), forest.get(3)[0]);
	}

	@Test
	public void testContains() {
		HashForest<SHA512HashValue> hf1 = new HashForest<SHA512HashValue>();
		HashForest<SHA512HashValue> hf2 = new HashForest<SHA512HashValue>();
		assertTrue(hf1.contains(hf2));
		assertTrue(hf2.contains(hf1));

		hf1.update(sha512Hashes.get(0));
		assertTrue(hf1.contains(hf2));

		hf2.update(sha512Hashes.get(0));
		assertTrue(hf1.contains(hf2));
		assertTrue(hf2.contains(hf1));

		hf1.update(sha512Hashes.get(1));
		hf1.update(sha512Hashes.get(2));
		assertTrue(hf1.contains(hf2));
		assertFalse(hf2.contains(hf1));

		hf2.update(sha512Hashes.get(1));
		assertTrue(hf1.contains(hf2));
		assertFalse(hf2.contains(hf1));

		hf2.update(sha512Hashes.get(3));
		assertFalse(hf1.contains(hf2));
		assertFalse(hf2.contains(hf1));

		HashForest<SHA512HashValue> hfComplete = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete.update(hv);
		}
		assertTrue(hfComplete.contains(hf1));
		
		// TODO: undefined case: should this be true or false?
//		assertFalse(hfComplete.contains(hf2));
		assertTrue(hfComplete.contains(hf2));
		
		assertFalse(hf1.contains(hfComplete));
		assertFalse(hf2.contains(hfComplete));

		// TODO: add more complex cases
	}

	@Test
	public void testValidate() {
		HashForest<SHA512HashValue> hf1 = new HashForest<SHA512HashValue>();
		HashForest<SHA512HashValue> hf2 = new HashForest<SHA512HashValue>();
		assertTrue(hf1.validate(hf2));
		assertTrue(hf2.validate(hf1));

		hf1.update(sha512Hashes.get(0));
		assertFalse(hf1.validate(hf2));
		assertFalse(hf2.validate(hf1));

		hf1.update(sha512Hashes.get(1));
		hf1.update(sha512Hashes.get(2));
		hf1.update(sha512Hashes.get(3));

		hf2.update(sha512Hashes.get(0));
		hf2.update(sha512Hashes.get(1));
		hf2.update(sha512Hashes.get(2));
		hf2.update(sha512Hashes.get(3));

		assertTrue(hf1.validate(hf2));
		assertTrue(hf2.validate(hf1));

		hf1.update(sha512Hashes.get(4));
		hf2.update(sha512Hashes.get(5));
		assertFalse(hf1.validate(hf2));
		assertFalse(hf2.validate(hf1));

		HashForest<SHA512HashValue> hfComplete = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete.update(hv);
		}
		assertFalse(hfComplete.validate(hf1));
		assertFalse(hfComplete.validate(hf2));

		HashForest<SHA512HashValue> hfComplete2 = new HashForest<SHA512HashValue>();
		assertTrue(hfComplete2.isEmpty());
		for (SHA512HashValue hv : sha512Hashes) {
			hfComplete2.update(hv);
		}
		assertTrue(hfComplete.validate(hfComplete2));

		// TODO: add more complex cases
	}
	
	@Test
	public void testDateTimeHandling() throws IOException, InvalidInputException {
		HashForest<SHA512HashValue> hf1 = new HashForest<SHA512HashValue>();
		
		hf1.update(sha512Hashes.get(0));
		
		assertNull(hf1.getFirstSerializedDateTime());
		
		StringWriter sw = new StringWriter();
		hf1.writeTo(sw);
		
		//String dateTime1 = DateProvider.date2String(hf.getFirstSerializedDateTime());
		String dateTime1 = hf1.getFirstSerializedDateTime();
		
		StringReader sr = new StringReader(sw.toString());
		HashForest<SHA512HashValue> hf2 = new HashForest<SHA512HashValue>();
		hf2.readFrom(sr);
		//String dateTime2 = DateProvider.date2String(hf2.getFirstSerializedDateTime());
		String dateTime2 = hf2.getFirstSerializedDateTime();
		
		assertEquals(dateTime1, dateTime2);
		
		hf2.update(sha512Hashes.get(1));
		sw = new StringWriter();
		hf2.writeTo(sw);
		//String dateTime3 = DateProvider.date2String(hf2.getFirstSerializedDateTime());
		String dateTime3 = hf2.getFirstSerializedDateTime();
		
		assertNotEquals(dateTime2, dateTime3);
		
		hf2.pruneForest();
		sw = new StringWriter();
		hf2.writeTo(sw);
		//String dateTime4 = DateProvider.date2String(hf2.getFirstSerializedDateTime());
		String dateTime4 = hf2.getFirstSerializedDateTime();
		
		assertEquals(dateTime3, dateTime4);
		
		sr = new StringReader(sw.toString());
		HashForest<SHA512HashValue> hf3 = new HashForest<SHA512HashValue>();
		hf3.readFrom(sr);
		//String dateTime5 = DateProvider.date2String(hf3.getFirstSerializedDateTime());
		String dateTime5 = hf3.getFirstSerializedDateTime();
		
		assertEquals(dateTime3, dateTime5);
	}

}
