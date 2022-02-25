package matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: kjsx
 * @Date: 2022/02/12/15:28
 * @Description:正则匹配敏感目录
 */
public class PathMatcher {
    private static String pathReg = "(?:\"|')(((?:[a-zA-Z]{1,10}://|//)[^\"'/]{1,}\\.[a-zA-Z]{2,}[^\"']{0,})|((?:/|\\.\\./|\\./)[^\"'><,;|*()(%%$^/\\\\\\[\\]][^\"'><,;|()]{1,})|([a-zA-Z0-9_\\-/]{1,}/[a-zA-Z0-9_\\-/]{1,}\\.(?:[a-zA-Z]{1,4}|action)(?:[\\?|#][^\"|']{0,}|))|([a-zA-Z0-9_\\-/]{1,}/[a-zA-Z0-9_\\-/]{3,}(?:[\\?|#][^\"|']{0,}|))|([a-zA-Z0-9_\\-]{1,}\\.(?:php|asp|aspx|jsp|json|action|html|js|txt|xml)(?:[\\?|#][^\"|']{0,}|)))(?:\"|')";
//    private static String body = "newdeploymentBusUpdateBus:\"/index.php?_g=newdeployment&_m=bus&_a=updateBus\",newdeploymentBusDeleteBus:\"/index.php?_g=newdeployment&_m=bus&_a=deleteBus\",newdeploymentBusGetBusUser:\"/index.php?_g=newdeployment&_m=bus&_a=getBusUser\",newdeploymentBusUpdateBusUser:\"/index.php?_g=newdeployment&_m=bus&_a=updateBus\"";
    //存放结果字符串
    private List<String> resultString;

    public void setResultString(List<String> resultString) {
        this.resultString = resultString;
    }

    public List<String> getResultString() {
        return resultString;
    }

    //匹配路径
    public List<String> searchPath(String body){
        List<String> searchPathList = new ArrayList<>();
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pathReg);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(body);
        while (m.find( )) {
            searchPathList.add(m.group(1));
        }
        return searchPathList;
    }

    //去重路径
    public List<String> removeDuplicates(List<String> list){
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(list);
        ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);
        return listWithoutDuplicates;
    }

    //判断获取的路径是否为空

    public PathMatcher(String body) {
        List<String> searchPathList = searchPath(body);
        List<String> listWithoutDuplicates = removeDuplicates(searchPathList);
        setResultString(listWithoutDuplicates);
    }

/*    public static void main(String[] args) {
        List<String> searchPathList = new ArrayList<>();
        if (searchPathList.isEmpty()){
            System.out.println("Found value: " + searchPathList );
        }
    }*/
    /*public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,3,3, 2, 1,2,3);
        LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(list);
        ArrayList<Integer> listWithoutDuplicates = new ArrayList<>(hashSet);
        System.out.println(listWithoutDuplicates);
    }*/
}
