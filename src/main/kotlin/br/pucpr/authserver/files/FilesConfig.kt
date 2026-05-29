package br.pucpr.authserver.files

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class FilesConfig {
    @Profile("!fs")
    @Bean("fileStorage")
    fun s3Storage() = S3Storage()

    @Profile("fs")
    @Bean("fileStorage")
    fun localStorage() = FileSystemStorage()


}