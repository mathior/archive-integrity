package de.cbraeutigam.archint.experiment;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import de.cbraeutigam.archint.hashforest.HashValue;
import de.cbraeutigam.archint.hashforest.SHA512HashValue;

public class HashValueExp {
	
	private static String[] hashStrings = new String[]{
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
			"0c27f942e4900da1dd6b0ef1e30fa34e713b65672aad1989d526dbd62feb94ef24ca90e164420d68fc0f75c6ac130befe7205eb06830ad04504ed38211f4383a"
	};
	
	private static String[] fileNames = new String[]{
			"00",
			"01",
			"02",
			"03",
			"04",
			"05",
			"06",
			"07",
			"08",
			"09",
			"10",
			"11",
			"12",
			"13",
			"14",
			"15",
			"16",
			"17",
			"18",
			"19",
			"20",
			"21",
			"22"
	};
	
	private static int[] computeLeafsPerTree(int leafsCount) {
		// compute numer of complete trees by counting the 1-bits in the
		// two-complement-representation
		int trees = Integer.bitCount(leafsCount);
		int[] treeSizes = new int[trees];
		
		int exp = 0;
		int idx = trees - 1;
		
		while (idx >= 0) {
			if ( ((1<<exp) & leafsCount) != 0) {
				treeSizes[idx] = 1<<exp;
				--idx;
			}
			++exp;
		}
		return treeSizes;
	}
	
	private static <T extends HashValue> HashValue[] createTree(List<T> leafs)
			throws NoSuchAlgorithmException {
		
		// a complete tree with n leafes has exact 2*n - 1 nodes
		int treeSize = 2 * leafs.size() - 1;
		HashValue[] tree = new HashValue[treeSize];
		for (int treeIdx = treeSize - 1, leafIdx = leafs.size() - 1;
				leafIdx >= 0;
				--treeIdx, --leafIdx) {
			tree[treeIdx] = leafs.get(leafIdx);
		}
		// in the array-based (0-based index) representation of a binary tree a
		// parent at index n has the children at 2*n+1 (left) and 2*n+2 (right)
		for (int treeIdx = treeSize - leafs.size() - 1;
				treeIdx >= 0;
				--treeIdx) {
			tree[treeIdx] = tree[2*treeIdx+1].concatenate(tree[2*treeIdx+2]);
		}
		return tree;
	}
	
	private static <T extends HashValue> List<HashValue[]> createForest(List<T> leafs)
			throws NoSuchAlgorithmException {
		int[] leafesPerTree = computeLeafsPerTree(leafs.size());
		
		List<HashValue[]> forest = new ArrayList<HashValue[]>();
		
		int startIdx = 0;
		for (int leafSize : leafesPerTree) {
			forest.add(createTree(leafs.subList(startIdx, startIdx + leafSize)));
			startIdx += leafSize;
		}
		
		return forest;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		List<SHA512HashValue> hashes = new ArrayList<SHA512HashValue>();
		for (String s : hashStrings) {
			hashes.add(new SHA512HashValue(s));
		}
		
		int n = 0;
		for (HashValue[] tree : createForest(hashes)) {
			System.out.println("tree " + n++);
			for (HashValue hv : tree) {
				System.out.println(hv);
			}
		}
		
	}

}
