package com.example.systemrezerwacji.domain.codemodule;

class CodeConfiguration {

    CodeFacade createForTest(CodeRepository codeRepository) {
        CodeService codeService = new CodeService(codeRepository);
        return new CodeFacade(codeService);
    }
}
