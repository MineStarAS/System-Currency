# [Minecraft] Currency Plugin - 여러 화폐를 하나의 플러그인으로 !

<p align="center">
    <img src="https://www.koreaminecraft.net/files/attach/images/2106929/174/256/003/d9063244c1b8d438c6f817cec6dd6ff5.jpg">
</p>

---

## | 플러그인 정보

* 이름 - Currency
* 개발자 - MineStar
* 저작권 - GNU General Public License v3.0

## | 플러그인 설명

이 플러그인은 여러 화폐를 생성하여 사용 할 수 있는 플러그인 입니다.

예를 들어 원 단위의 화폐와 캐시 단위의 화폐를 만들어 사용 할 수 있습니다.

## | API
* [GitHub](https://github.com/MineStarAS/System-Currency-API)
* [JitPack](https://jitpack.io/#MineStarAS/System-Currency-API)

### Gralde
```gralde
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
    
dependencies {
    compileOnly 'com.github.MineStarAS:System-Currency-API:1.0.0'
}
```

### Gralde Koltin
```kotlin
allprojects {
    repositories {
        maven(url = "https://jitpack.io/")
    }
}
    
dependencies {
    compileOnly("com.github.MineStarAS:System-Currency-API:1.0.0")
}
```
### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
	<groupId>com.github.MineStarAS</groupId>
	<artifactId>System-Currency-API</artifactId>
	<version>Tag</version>
</dependency>
```

